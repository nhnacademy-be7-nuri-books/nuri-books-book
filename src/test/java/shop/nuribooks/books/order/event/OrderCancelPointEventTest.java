package shop.nuribooks.books.order.event;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.nuribooks.books.book.point.entity.PointHistory;
import shop.nuribooks.books.book.point.service.PointHistoryService;
import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.order.event.OrderCancelPointEvent;
import shop.nuribooks.books.order.order.event.handler.OrderCancelEventHandler;

@ExtendWith(MockitoExtension.class)
class OrderCancelPointEventTest {
	@InjectMocks
	OrderCancelEventHandler orderCancelEventHandler;

	@Mock
	PointHistoryService pointHistoryService;

	private OrderCancelPointEvent orderCancelPointEvent;
	private Member member;

	@BeforeEach
	void setUp() {
		Grade grade = TestUtils.creategrade();
		Customer customer = TestUtils.createCustomer();
		Order order = TestUtils.createOrder(customer);

		member = TestUtils.createMember(customer, grade);
		orderCancelPointEvent = new OrderCancelPointEvent(member, order, BigDecimal.valueOf(1000));
	}

	@Test
	void createPointHistory() {
		member.setPoint(BigDecimal.valueOf(10000));
		when(pointHistoryService.registerPointHistory(any(), any())).thenReturn(new PointHistory());

		orderCancelEventHandler.createPointHistory(orderCancelPointEvent);

		verify(pointHistoryService).registerPointHistory(any(), any());
	}
}
