package shop.nuribooks.books.book.review.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.point.service.PointHistoryService;
import shop.nuribooks.books.book.review.dto.ReviewImageDto;
import shop.nuribooks.books.book.review.dto.request.ReviewRequest;
import shop.nuribooks.books.book.review.dto.request.ReviewUpdateRequest;
import shop.nuribooks.books.book.review.dto.response.ReviewBookResponse;
import shop.nuribooks.books.book.review.dto.response.ReviewImageResponse;
import shop.nuribooks.books.book.review.dto.response.ReviewMemberResponse;
import shop.nuribooks.books.book.review.dto.response.ReviewScoreResponse;
import shop.nuribooks.books.book.review.entity.Review;
import shop.nuribooks.books.book.review.repository.ReviewImageRepository;
import shop.nuribooks.books.book.review.repository.ReviewRepository;
import shop.nuribooks.books.book.review.service.impl.ReviewServiceImpl;
import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;
import shop.nuribooks.books.exception.book.BookIdNotFoundException;
import shop.nuribooks.books.exception.common.RequiredHeaderIsNullException;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.exception.review.NoOrderDetailForReviewException;
import shop.nuribooks.books.exception.review.ReviewNotFoundException;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.orderdetail.entity.OrderDetail;
import shop.nuribooks.books.order.orderdetail.repository.OrderDetailRepository;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
	@Mock
	private ApplicationEventPublisher publisher;
	@InjectMocks
	private ReviewServiceImpl reviewService;
	@Mock
	private BookRepository bookRepository;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private ReviewRepository reviewRepository;
	@Mock
	private ReviewImageRepository reviewImageRepository;
	@Mock
	private OrderDetailRepository orderDetailRepository;
	@Mock
	private PointHistoryService pointHistoryService;
	private Book book;
	private Member member;
	private Review review;
	private ReviewRequest reviewRequest;
	private ReviewUpdateRequest reviewUpdateRequest;
	private ReviewImageResponse reviewImageResponse;
	private OrderDetail orderDetail;

	@BeforeEach
	void setUp() {
		member = TestUtils.createMember(TestUtils.createCustomer(), TestUtils.creategrade());
		TestUtils.setIdForEntity(member, 1L);

		this.book = TestUtils.createBook(TestUtils.createPublisher());

		TestUtils.setIdForEntity(book, 1L);

		Order order = TestUtils.createOrder(member.getCustomer());

		this.orderDetail = TestUtils.createOrderDetail(order, book);
		TestUtils.setIdForEntity(orderDetail, 1L);

		reviewRequest = new ReviewRequest(
			"title",
			"content",
			4,
			book.getId(),
			List.of("http://example.com/image1.jpg", "http://example.com/image2.jpg")
		);

		reviewUpdateRequest = new ReviewUpdateRequest(
			"title",
			"content",
			4,
			book.getId()
		);

		review = Review.builder()
			.title("title")
			.content("content")
			.score(4)
			.member(member)
			.book(book)
			.orderDetail(orderDetail)
			.build();
		ReflectionTestUtils.setField(review, "id", 1L);
		reviewImageResponse = ReviewImageResponse.builder().id(1L).imageUrl("good").build();
	}

	@Test
	void registerMemberNotFound() {
		when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());
		MemberIdContext.setMemberId(member.getId());
		assertThrows(MemberNotFoundException.class,
			() -> reviewService.registerReview(reviewRequest));
	}

	@Test
	void registerOrderDetailNotFound() {
		when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
		when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
		when(orderDetailRepository.findByBookIdAndOrderCustomerIdAndReviewIsNullAndOrderStateIn(anyLong(), anyLong(),
			anyList())).thenReturn(
			List.of());
		MemberIdContext.setMemberId(member.getId());
		assertThrows(NoOrderDetailForReviewException.class,
			() -> reviewService.registerReview(reviewRequest));
	}

	@Test
	void registerSuccessWithPolicyName() {
		ReviewRequest newReviewRequest = new ReviewRequest("제에목", "내앵애애애애용", 1, book.getId(), null);
		Review newReview = newReviewRequest.toEntity(member, book, orderDetail);
		TestUtils.setIdForEntity(newReview, 1L);
		when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
		when(reviewRepository.save(any())).thenReturn(newReview);
		when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
		when(orderDetailRepository.findByBookIdAndOrderCustomerIdAndReviewIsNullAndOrderStateIn(anyLong(), anyLong(),
			anyList())).thenReturn(
			List.of(orderDetail));
		// when(pointHistoryService.registerPointHistory(any(), any())).thenReturn(new PointHistory());
		MemberIdContext.setMemberId(member.getId());
		assertEquals(ReviewMemberResponse.of(newReview),
			reviewService.registerReview(newReviewRequest));
	}

	@Test
	void registerSuccess() {
		when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
		Review review1 = reviewRequest.toEntity(member, book, orderDetail);
		TestUtils.setIdForEntity(review1, 1L);
		when(reviewRepository.save(any())).thenReturn(review1);
		when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
		when(orderDetailRepository.findByBookIdAndOrderCustomerIdAndReviewIsNullAndOrderStateIn(anyLong(), anyLong(),
			anyList())).thenReturn(
			List.of(orderDetail));
		// when(pointHistoryService.registerPointHistory(any(), any())).thenReturn(new PointHistory());
		MemberIdContext.setMemberId(member.getId());
		assertEquals(ReviewMemberResponse.of(review1),
			reviewService.registerReview(reviewRequest));
	}

	@Test
	void registerByNullUser() {
		MemberIdContext.setMemberId(null);
		assertThrows(RequiredHeaderIsNullException.class,
			() -> reviewService.registerReview(reviewRequest));
	}

	@Test
	void updateFailed() {
		when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());
		MemberIdContext.setMemberId(member.getId());
		assertThrows(ReviewNotFoundException.class,
			() -> reviewService.updateReview(reviewUpdateRequest, review.getId()));
	}

	@Test
	void updatedByOtherUser() {
		when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));
		MemberIdContext.setMemberId(member.getId() + 10);
		assertThrows(ReviewNotFoundException.class,
			() -> reviewService.updateReview(reviewUpdateRequest, review.getId()));
	}

	@Test
	void updatedByNullUser() {
		MemberIdContext.setMemberId(null);
		assertThrows(RequiredHeaderIsNullException.class,
			() -> reviewService.updateReview(reviewUpdateRequest, review.getId()));
	}

	@Test
	void updateSuccess() {
		when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));
		MemberIdContext.setMemberId(member.getId());
		assertEquals(ReviewMemberResponse.of(review),
			reviewService.updateReview(reviewUpdateRequest, review.getId()));
	}

	@Test
	void getScoreFail() {
		when(bookRepository.existsById(anyLong())).thenReturn(false);
		assertThrows(BookIdNotFoundException.class,
			() -> this.reviewService.getScoreByBookId(1));
	}

	@Test
	void getScoreSuccess() {
		double score = 4.1;
		when(bookRepository.existsById(anyLong())).thenReturn(true);
		when(reviewRepository.findScoreByBookId(1)).thenReturn(score);

		ReviewScoreResponse response = reviewService.getScoreByBookId(1);

		assertEquals(score, response.avgScore());
	}

	@Test
	void getReviewsAndMemFail() {
		when(bookRepository.existsById(anyLong())).thenReturn(false);
		assertThrows(BookIdNotFoundException.class,
			() -> this.reviewService.getReviewsByBookId(1, PageRequest.of(0, 1)));
	}

	@Test
	void getReviewsAndMemSuccess() {
		List<ReviewMemberResponse> res = new LinkedList<>();
		when(bookRepository.existsById(anyLong())).thenReturn(true);
		when(reviewRepository.findReviewsByBookId(anyLong(), any())).thenReturn(res);
		Page<ReviewMemberResponse> pageRes = reviewService.getReviewsByBookId(1, PageRequest.of(0, 1));
		assertEquals(0, pageRes.getContent().size());
		assertEquals(0, pageRes.getPageable().getPageNumber());
		assertEquals(1, pageRes.getPageable().getPageSize());
	}

	@Test
	void getReviewsAndMemFullSuccess() {
		List<ReviewMemberResponse> res = List.of(ReviewMemberResponse.of(review));
		when(bookRepository.existsById(anyLong())).thenReturn(true);
		when(reviewRepository.findReviewsByBookId(anyLong(), any())).thenReturn(res);
		List<ReviewImageDto> reviewImages = List.of(new ReviewImageDto(1L, reviewImageResponse));
		when(reviewImageRepository.findReviewImagesByReviewIds(anyList())).thenReturn(reviewImages);
		Page<ReviewMemberResponse> pageRes = reviewService.getReviewsByBookId(1, PageRequest.of(0, 1));
		assertEquals(1, pageRes.getContent().size());
		assertEquals(0, pageRes.getPageable().getPageNumber());
		assertEquals(1, pageRes.getPageable().getPageSize());
	}

	@Test
	void getReviewsAndBookFail() {
		when(memberRepository.existsById(anyLong())).thenReturn(false);
		assertThrows(MemberNotFoundException.class,
			() -> this.reviewService.getReviewsByMemberId(1, PageRequest.of(0, 2)));
	}

	@Test
	void getReviewsAndBookSuccess() {
		List<ReviewBookResponse> res = new LinkedList<>();
		when(memberRepository.existsById(anyLong())).thenReturn(true);
		when(reviewRepository.findReviewsByMemberId(anyLong(), any())).thenReturn(res);
		Page<ReviewBookResponse> pageRes = reviewService.getReviewsByMemberId(1, PageRequest.of(0, 1));
		assertEquals(0, pageRes.getContent().size());
		assertEquals(0, pageRes.getNumber());
		assertEquals(1, pageRes.getSize());
	}

	@Test
	void getReviewsAndBookFullSuccess() {
		List<ReviewBookResponse> res = List.of(ReviewBookResponse.of(review));
		when(memberRepository.existsById(anyLong())).thenReturn(true);
		when(reviewRepository.findReviewsByMemberId(anyLong(), any())).thenReturn(res);
		List<ReviewImageDto> reviewImages = List.of(new ReviewImageDto(1L, reviewImageResponse));
		when(reviewImageRepository.findReviewImagesByReviewIds(anyList())).thenReturn(reviewImages);
		Page<ReviewBookResponse> pageRes = reviewService.getReviewsByMemberId(1, PageRequest.of(0, 1));
		assertEquals(1, pageRes.getContent().size());
		assertEquals(0, pageRes.getNumber());
		assertEquals(1, pageRes.getSize());
	}
}
