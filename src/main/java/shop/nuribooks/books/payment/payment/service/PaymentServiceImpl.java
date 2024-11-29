package shop.nuribooks.books.payment.payment.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.common.config.rabbitmq.RabbitmqConfig;
import shop.nuribooks.books.common.message.ResponseMessage;
import shop.nuribooks.books.exception.order.OrderNotFoundException;
import shop.nuribooks.books.exception.payment.FailedPaymentCancelException;
import shop.nuribooks.books.exception.payment.PaymentNotFoundException;
import shop.nuribooks.books.inventory.message.InventoryUpdateMessage;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.order.repository.OrderRepository;
import shop.nuribooks.books.order.orderdetail.entity.OrderDetail;
import shop.nuribooks.books.order.orderdetail.entity.OrderState;
import shop.nuribooks.books.order.orderdetail.repository.OrderDetailRepository;
import shop.nuribooks.books.payment.payment.dto.PaymentSuccessRequest;
import shop.nuribooks.books.payment.payment.entity.Payment;
import shop.nuribooks.books.payment.payment.entity.PaymentCancel;
import shop.nuribooks.books.payment.payment.entity.PaymentMethod;
import shop.nuribooks.books.payment.payment.entity.PaymentState;
import shop.nuribooks.books.payment.payment.event.PointSavedEvent;
import shop.nuribooks.books.payment.payment.repository.PaymentCancelRepository;
import shop.nuribooks.books.payment.payment.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PaymentServiceImpl implements PaymentService {

	private final MemberRepository memberRepository;
	private final OrderRepository orderRepository;
	private final PaymentRepository paymentRepository;
	private final OrderDetailRepository orderDetailRepository;
	private final PaymentCancelRepository paymentCancelRepository;

	private final RabbitTemplate rabbitTemplate;
	private final ApplicationEventPublisher publisher;

	/**
	 * 결제 완료 처리
	 *
	 * @param paymentSuccessRequest 결제 테이블 저장용
	 * @return 성공/실패 메시지
	 */
	@Override
	@Transactional
	public ResponseMessage donePayment(PaymentSuccessRequest paymentSuccessRequest) {

		String tossOrderId = paymentSuccessRequest.orderId();
		Long orderId = Math.abs(Long.parseLong(tossOrderId.substring(10)));
		Order order = orderRepository.findById(orderId).orElseThrow(
			() -> new OrderNotFoundException("해당되는 주문을 찾을 수 없습니다.")
		);

		// 결제 정보 저장
		Payment payment = Payment.builder()
			.order(order)
			.tossPaymentKey(paymentSuccessRequest.paymentKey())
			.paymentMethod(PaymentMethod.fromKoreanName(paymentSuccessRequest.method()))
			.paymentState(PaymentState.valueOf(paymentSuccessRequest.status()))
			.unitPrice(BigDecimal.valueOf(paymentSuccessRequest.totalAmount()))
			.requestedAt(paymentSuccessRequest.requestedAt())
			.approvedAt(paymentSuccessRequest.approvedAt())
			.build();

		paymentRepository.save(payment);

		// 주문 상태 변경 & 재고 차감
		List<OrderDetail> orderDetailList = orderDetailRepository.findAllByOrderId(orderId);

		for (OrderDetail orderDetail : orderDetailList) {
			orderDetail.setOrderState(OrderState.PAID);
		}

		//재고 업데이트 메시지 발행
		try {
			publishInventoryUpdateMessages(orderDetailList);
		} catch (AmqpRejectAndDontRequeueException e) {
			log.error("Invalid Message Format : {}", e.getMessage());
		}

		orderDetailRepository.saveAll(orderDetailList);

		// 회원 사용 총금액 반영
		Optional<Member> member = memberRepository.findById(order.getCustomer().getId());
		member.ifPresent(value -> {
			member.get().setTotalPaymentAmount(BigDecimal.valueOf(paymentSuccessRequest.totalAmount()));
			memberRepository.save(member.get());
		});

		// 포인트 적립
		handlePointSaving(order);

		log.debug("결제 성공");

		return ResponseMessage.builder().message("성공").statusCode(201).build();
	}

	@Override
	public ResponseMessage cancelPayment(Order order, String reason) {

		JSONParser parser = new JSONParser();

		// 결제 정보 가져오기
		Payment payment = paymentRepository.findByOrder(order).orElseThrow(
			() -> new PaymentNotFoundException("결제 정보가 없습니다.")
		);

		// 결제 취소
		JSONObject paymentCancelInfo = new JSONObject();
		paymentCancelInfo.put("cancelReason", reason);

		String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";

		Base64.Encoder encoder = Base64.getEncoder();
		byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
		String authorizations = "Basic " + new String(encodedBytes);

		String paymentKey = payment.getTossPaymentKey();
		String cancelUrl = "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel";
		JSONObject tossResponse;

		try {
			// 결제 취소 API 호출
			URL url = new URL(cancelUrl);

			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestProperty("Authorization", authorizations);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);

			OutputStream outputStream = connection.getOutputStream();
			outputStream.write(paymentCancelInfo.toString().getBytes(StandardCharsets.UTF_8));

			int code = connection.getResponseCode();

			boolean isSuccess = code == 200;

			InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

			Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
			tossResponse = (JSONObject)parser.parse(reader);
			responseStream.close();

			if (isSuccess) {
				// 결제 상태 변경 & 취소 사유 작성
				payment.setPaymentState(PaymentState.CANCELED);
				paymentRepository.save(payment);

				JSONArray cancelArray = (JSONArray)tossResponse.get("cancels");
				JSONObject cancelObject = (JSONObject)cancelArray.get(0);

				PaymentCancel paymentCancel = PaymentCancel.builder()
					.payment(payment)
					.transactionKey((String)cancelObject.get("transactionKey"))
					.cancelReason((String)cancelObject.get("cancelReason"))
					.canceledAt(OffsetDateTime.parse((String)cancelObject.get("canceledAt")).toLocalDateTime())
					.build();

				paymentCancelRepository.save(paymentCancel);

				return ResponseMessage.builder().message("주문 취소 성공").statusCode(200).build();
			}

		} catch (IOException | ParseException e) {
			throw new FailedPaymentCancelException("주문 취소 처리 중 IO 예외가 발생하였습니다.");
		}

		log.error("토스 페이먼츠를 통해 결제 취소를 진행 중 예외 발생");
		throw new FailedPaymentCancelException((String)tossResponse.get("message"));
	}

	// ---------------------------------- private

	/**
	 * 적립금 처리
	 * @param order 주문 정보
	 */
	private void handlePointSaving(Order order) {
		Optional<Member> member = memberRepository.findById(order.getCustomer().getId());

		member.ifPresent(value -> {
			publisher.publishEvent(new PointSavedEvent(value, order, order.getBooksPrice()));
		});
	}

	//재고 업데이트 메시지 발행
	private void publishInventoryUpdateMessages(List<OrderDetail> orderDetailList) {
		for (OrderDetail orderDetail : orderDetailList) {
			InventoryUpdateMessage message = InventoryUpdateMessage
				.builder()
				.bookId(orderDetail.getBook().getId())
				.count(orderDetail.getCount())
				.messageId("inventory-" + UUID.randomUUID())
				.build();

			rabbitTemplate.convertAndSend(
				RabbitmqConfig.INVENTORY_EXCHANGE,
				RabbitmqConfig.INVENTORY_ROUTING_KEY,
				message
			);
		}
	}
}
