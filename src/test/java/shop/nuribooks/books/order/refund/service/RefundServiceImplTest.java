package shop.nuribooks.books.order.refund.service;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.point.service.PointHistoryService;
import shop.nuribooks.books.book.publisher.entity.Publisher;
import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.orderDetail.entity.OrderDetail;
import shop.nuribooks.books.order.orderDetail.entity.OrderState;
import shop.nuribooks.books.order.orderDetail.service.OrderDetailService;
import shop.nuribooks.books.order.refund.dto.request.RefundRequest;
import shop.nuribooks.books.order.refund.entity.Refund;
import shop.nuribooks.books.order.refund.repository.RefundRepository;

@ExtendWith(MockitoExtension.class)
class RefundServiceImplTest {

	@InjectMocks
	private RefundServiceImpl refundService;

	@Mock
	private RefundRepository refundRepository;

	@Mock
	private OrderDetailService orderDetailService;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PointHistoryService pointHistoryService;

	@DisplayName("환불시 주문상태, 재고가 변경되고 금액을 포인트로 반환한다.")
	@Test
	void refund() {
		// given
		Order order = createOrder();
		Publisher publisher = TestUtils.createPublisher();
		Book book = TestUtils.createBook(publisher);
		int bookStock = book.getStock();
		int quantity = 3;
		BigDecimal price = BigDecimal.valueOf(10000L);
		OrderDetail orderDetail = new OrderDetail(order, OrderState.COMPLETED, book, quantity, price,
			true);
		when(orderDetailService.getOrderDetail(any())).thenReturn(orderDetail);

		Customer customer = TestUtils.createCustomer();
		Grade creategrade = TestUtils.creategrade();
		Member member = TestUtils.createMember(customer, creategrade);
		Refund refund = Refund.builder()
			.orderDetail(orderDetail)
			.refundAmount(price.multiply(BigDecimal.valueOf(quantity)))
			.reason("이유")
			.build();
		when(refundRepository.save(any())).thenReturn(refund);
		when(memberRepository.findById(any())).thenReturn(Optional.of(member));

		RefundRequest refundRequest = new RefundRequest(orderDetail.getId(), "이유");
		// when
		refundService.refund(refundRequest);

		// then
		verify(pointHistoryService).registerPointHistory(any(), any());

	}

	private Order createOrder() {
		Customer customer = Customer.builder()
			.name("정누리")
			.password("Passw@rd1")
			.phoneNumber("01012345678")
			.email("nuri1234@example.com")
			.build();

		BigDecimal paymentPrice = new BigDecimal("50000");
		LocalDateTime orderedAt = LocalDateTime.of(2024, 11, 10, 14, 30);
		LocalDate expectedDeliveryAt = LocalDate.of(2024, 11, 11);

		return Order.builder()
			.customer(customer)
			.paymentPrice(paymentPrice)
			.orderedAt(orderedAt)
			.expectedDeliveryAt(expectedDeliveryAt)
			.build();
	}

}
