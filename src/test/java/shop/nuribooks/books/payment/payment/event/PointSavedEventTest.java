package shop.nuribooks.books.payment.payment.event;

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
import shop.nuribooks.books.payment.payment.event.handler.PointSavedEventHandler;

@ExtendWith(MockitoExtension.class)
class PointSavedEventTest {
	@InjectMocks
	PointSavedEventHandler pointSavedEventHandler;

	@Mock
	PointHistoryService pointHistoryService;

	private PointSavedEvent pointSavedEvent;

	@BeforeEach
	void setUp() {
		Grade grade = TestUtils.creategrade();
		Customer customer = TestUtils.createCustomer();
		Order order = TestUtils.createOrder(customer);

		Member member = TestUtils.createMember(customer, grade);
		pointSavedEvent = new PointSavedEvent(member, order, BigDecimal.valueOf(10));
	}

	@Test
	void createPointHistory() {
		when(pointHistoryService.registerPointHistory(any(), any())).thenReturn(new PointHistory());

		pointSavedEventHandler.createPointHistory(pointSavedEvent);

		verify(pointHistoryService).registerPointHistory(any(), any());
	}
}
