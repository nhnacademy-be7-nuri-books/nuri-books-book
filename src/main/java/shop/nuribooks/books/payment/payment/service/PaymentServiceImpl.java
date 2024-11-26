package shop.nuribooks.books.payment.payment.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.common.message.ResponseMessage;
import shop.nuribooks.books.exception.order.OrderNotFoundException;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.order.repository.OrderRepository;
import shop.nuribooks.books.order.orderdetail.entity.OrderDetail;
import shop.nuribooks.books.order.orderdetail.entity.OrderState;
import shop.nuribooks.books.order.orderdetail.repository.OrderDetailRepository;
import shop.nuribooks.books.payment.payment.dto.PaymentSuccessRequest;
import shop.nuribooks.books.payment.payment.entity.Payment;
import shop.nuribooks.books.payment.payment.entity.PaymentMethod;
import shop.nuribooks.books.payment.payment.entity.PaymentState;
import shop.nuribooks.books.payment.payment.event.PointSavedEvent;
import shop.nuribooks.books.payment.payment.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

	private final MemberRepository memberRepository;
	private final OrderRepository orderRepository;
	private final PaymentRepository paymentRepository;
	private final OrderDetailRepository orderDetailRepository;
	private final BookRepository bookRepository;
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
			Book book = orderDetail.getBook();
			book.updateStock(orderDetail.getCount());
		}

		bookRepository.saveAll(orderDetailList.stream()
			.map(OrderDetail::getBook)  // Book 객체만 추출
			.toList());  // 일괄 저장

		orderDetailRepository.saveAll(orderDetailList);

		// 회원 사용 총금액 반영
		Optional<Member> member = memberRepository.findById(order.getCustomer().getId());
		member.ifPresent(value -> {
			member.get().setTotalPaymentAmount(BigDecimal.valueOf(paymentSuccessRequest.totalAmount()));
		});

		// 포인트 적립
		handlePointSaving(order);

		log.debug("결제 성공");

		return ResponseMessage.builder().message("성공").statusCode(201).build();
	}

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
}
