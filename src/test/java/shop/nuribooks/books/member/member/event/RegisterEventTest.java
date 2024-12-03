package shop.nuribooks.books.member.member.event;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.nuribooks.books.book.coupon.service.CouponService;
import shop.nuribooks.books.book.point.entity.child.ReviewSavingPoint;
import shop.nuribooks.books.book.point.service.PointHistoryService;
import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.event.handler.RegisterEventHandler;

@ExtendWith(MockitoExtension.class)
public class RegisterEventTest {
	@InjectMocks
	RegisterEventHandler registerEventHandler;

	@Mock
	private PointHistoryService pointHistoryService;
	@Mock
	private CouponService couponService;

	private RegisteredEvent registeredEvent;
	private Member member;

	@BeforeEach
	void setUp() {
		Grade grade = TestUtils.creategrade();
		Customer customer = TestUtils.createCustomer();
		member = TestUtils.createMember(customer, grade);
		registeredEvent = new RegisteredEvent(member);
	}

	@Test
	void createPointHistoryTest() {
		when(pointHistoryService.registerPointHistory(any(), any())).thenReturn(new ReviewSavingPoint());
		registerEventHandler.createPointHistory(registeredEvent);
		verify(pointHistoryService).registerPointHistory(any(), any());
	}

	@Test
	void createCouponTest() {
		registerEventHandler.createCoupon(registeredEvent);
		verify(couponService).issueWelcomeCoupon(any());
	}
}
