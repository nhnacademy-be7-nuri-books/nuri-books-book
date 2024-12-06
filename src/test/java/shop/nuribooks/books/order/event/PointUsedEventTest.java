package shop.nuribooks.books.order.event;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.nuribooks.books.book.point.entity.PointHistory;
import shop.nuribooks.books.book.point.exception.PointOverException;
import shop.nuribooks.books.book.point.service.PointHistoryService;
import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.order.event.PointUsedEvent;
import shop.nuribooks.books.order.order.event.handler.PointUsedEventHandler;

@ExtendWith(MockitoExtension.class)
class PointUsedEventTest {
	@InjectMocks
	PointUsedEventHandler pointUsedEventHandler;

	@Mock
	PointHistoryService pointHistoryService;

	private PointUsedEvent pointUsedEvent;
	private Member member;

	@BeforeEach
	void setUp() {
		Grade grade = TestUtils.creategrade();
		Customer customer = TestUtils.createCustomer();
		Order order = TestUtils.createOrder(customer);

		member = TestUtils.createMember(customer, grade);
		pointUsedEvent = new PointUsedEvent(member, order, BigDecimal.valueOf(10));
	}

	@Test
	void createPointHistory() {
		member.setPoint(BigDecimal.valueOf(10000));
		when(pointHistoryService.registerPointHistory(any(), any())).thenReturn(new PointHistory());

		pointUsedEventHandler.createPointHistory(pointUsedEvent);

		verify(pointHistoryService).registerPointHistory(any(), any());
	}

	@Test
	void createPointHistory_fail() {
		Assertions.assertThrows(PointOverException.class,
			() -> pointUsedEventHandler.createPointHistory(pointUsedEvent));
	}
}
