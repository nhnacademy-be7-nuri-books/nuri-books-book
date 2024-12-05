package shop.nuribooks.books.payment.payment.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import shop.nuribooks.books.payment.payment.entity.PaymentState;
import shop.nuribooks.books.payment.payment.event.PaymentEvent;
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
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
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
		Payment payment = paymentSuccessRequest.toEntity(order);

		paymentRepository.save(payment);

		// 주문 상태 변경 & 재고 차감
		List<OrderDetail> orderDetailList = orderDetailRepository.findAllByOrderId(orderId);

		for (OrderDetail orderDetail : orderDetailList) {
			orderDetail.setOrderState(OrderState.PAID);
			//재고 업데이트 메시지 발행
			publishInventoryUpdateMessages(orderDetail);
		}

		// 회원 사용 총금액 반영 및 포인트 적립위한 event
		createPaymentEvent(order);

		log.debug("결제 성공");

		return ResponseMessage.builder().message("성공").statusCode(201).build();
	}

	@Transactional
	@Override
	public ResponseMessage cancelPayment(Order order, String reason) {
		// 결제 정보 가져오기
		Payment payment = paymentRepository.findByOrder(order).orElseThrow(
			() -> new PaymentNotFoundException("결제 정보가 없습니다.")
		);

		// 결제 취소 로직 시작
		// 결제 취소 API request 생성 데이터 선언
		String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";

		Base64.Encoder encoder = Base64.getEncoder();
		byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
		String authorizations = "Basic " + new String(encodedBytes);

		String paymentKey = payment.getTossPaymentKey();
		String cancelUrl = "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel";

		HttpHeaders headers = createHeader(authorizations);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("cancelReason", reason);

		// request 생성 및 restTemplate 으로 response 요청.
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
		ResponseEntity<String> response = restTemplate.exchange(cancelUrl, HttpMethod.POST, requestEntity,
			String.class);

		if (response.getStatusCode().is2xxSuccessful()) {
			log.info("{}", response.getBody());
			//JSON 응답 파싱하여 데이터 추출
			payment.setPaymentState(PaymentState.CANCELED);
			JsonNode root;
			try {
				root = objectMapper.readTree(response.getBody());
			} catch (JsonProcessingException e) {
				throw new FailedPaymentCancelException("결제 취소 중 문제가 발생하였습니다." + e.getMessage());
			}

			// 주문 취소 객체 생성.
			JsonNode node = root.path("cancels").get(0);
			PaymentCancel paymentCancel = PaymentCancel.builder()
				.payment(payment)
				.transactionKey(node.path("transactionKey").asText())
				.cancelReason(node.path("cancelReason").asText())
				.canceledAt(LocalDateTime.parse(node.path("canceledAt").asText()))
				.build();
			paymentCancelRepository.save(paymentCancel);

			// 주문 상태 변경 & 재고 차감
			List<OrderDetail> orderDetailList = orderDetailRepository.findAllByOrderId(order.getId());

			for (OrderDetail orderDetail : orderDetailList) {
				orderDetail.setOrderState(OrderState.CANCELED);
				orderDetail.setCount(-orderDetail.getCount());

				//재고 업데이트 메시지 발행
				publishInventoryUpdateMessages(orderDetail);
			}

			return ResponseMessage.builder().message("주문 취소 성공").statusCode(200).build();
		} else {
			log.error("Failed... Status: {}, Response: {}", response.getStatusCode(),
				response.getBody());
			throw new FailedPaymentCancelException("결제 취소에 실패하였습니다.");
		}
	}

	// ---------------------------------- private

	/**
	 * 적립금 처리
	 * @param order 주문 정보
	 */
	private void createPaymentEvent(Order order) {
		Optional<Member> member = memberRepository.findById(order.getCustomer().getId());

		member.ifPresent(value ->
			publisher.publishEvent(new PaymentEvent(value, order, order.getBooksPrice()))
		);
	}

	//재고 업데이트 메시지 발행
	private void publishInventoryUpdateMessages(OrderDetail orderDetail) {
		InventoryUpdateMessage message = InventoryUpdateMessage
			.builder()
			.bookId(orderDetail.getBook().getId())
			.count(orderDetail.getCount())
			.messageId("inventory-" + UUID.randomUUID())
			.build();
		try {
			rabbitTemplate.convertAndSend(
				RabbitmqConfig.INVENTORY_EXCHANGE,
				RabbitmqConfig.INVENTORY_ROUTING_KEY,
				message
			);
		} catch (AmqpRejectAndDontRequeueException e) {
			log.error("Invalid Message Format : {}", e.getMessage());
		}
	}

	private HttpHeaders createHeader(String authorizations) {
		HttpHeaders headers = new org.springframework.http.HttpHeaders();
		headers.set("Authorization", authorizations);
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}
}
