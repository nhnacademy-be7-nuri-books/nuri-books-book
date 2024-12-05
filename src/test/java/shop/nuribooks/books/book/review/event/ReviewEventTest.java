package shop.nuribooks.books.book.review.event;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.point.entity.child.ReviewSavingPoint;
import shop.nuribooks.books.book.point.service.PointHistoryService;
import shop.nuribooks.books.book.publisher.entity.Publisher;
import shop.nuribooks.books.book.review.entity.Review;
import shop.nuribooks.books.book.review.event.handler.ReviewRegisterEventHandler;
import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.orderdetail.entity.OrderDetail;

@ExtendWith(MockitoExtension.class)
class ReviewEventTest {
	@InjectMocks
	ReviewRegisterEventHandler reviewRegisterEventHandler;
	@Mock
	PointHistoryService pointHistoryService;

	private ReviewRegisteredEvent reviewRegisteredEvent;
	private Review review;

	@BeforeEach
	void setUp() {
		Grade grade = TestUtils.creategrade();
		Customer customer = TestUtils.createCustomer();
		Publisher publisher = TestUtils.createPublisher();
		Book book = TestUtils.createBook(publisher);
		Order order = TestUtils.createOrder(customer);
		OrderDetail orderDetail = TestUtils.createOrderDetail(order, book);

		Member member = TestUtils.createMember(customer, grade);
		review = TestUtils.createReview(member, book, orderDetail);
		reviewRegisteredEvent = new ReviewRegisteredEvent(member, review);
	}

	@Test
	void createReviewImagePointHistoryTest() {
		when(pointHistoryService.registerPointHistory(any(), any())).thenReturn(new ReviewSavingPoint());
		reviewRegisterEventHandler.createPointHistory(reviewRegisteredEvent);
		verify(pointHistoryService).registerPointHistory(any(), any());
	}

	@Test
	void createReviewPointHistoryTest() {
		review.getReviewImages().clear();
		when(pointHistoryService.registerPointHistory(any(), any())).thenReturn(new ReviewSavingPoint());
		reviewRegisterEventHandler.createPointHistory(reviewRegisteredEvent);
		verify(pointHistoryService).registerPointHistory(any(), any());
	}
}
