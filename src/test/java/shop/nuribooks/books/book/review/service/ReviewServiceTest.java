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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.review.dto.ReviewImageDto;
import shop.nuribooks.books.book.review.dto.request.ReviewRequest;
import shop.nuribooks.books.book.review.dto.response.ReviewBookResponse;
import shop.nuribooks.books.book.review.dto.response.ReviewImageResponse;
import shop.nuribooks.books.book.review.dto.response.ReviewMemberResponse;
import shop.nuribooks.books.book.review.entity.Review;
import shop.nuribooks.books.book.review.repository.ReviewImageRepository;
import shop.nuribooks.books.book.review.repository.ReviewRepository;
import shop.nuribooks.books.book.review.service.impl.ReviewServiceImpl;
import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.common.message.PagedResponse;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;
import shop.nuribooks.books.exception.book.BookIdNotFoundException;
import shop.nuribooks.books.exception.common.RequiredHeaderIsNullException;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.exception.order.detail.OrderDetailNotFoundException;
import shop.nuribooks.books.exception.review.ReviewNotFoundException;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.orderDetail.entity.OrderDetail;
import shop.nuribooks.books.order.orderDetail.repository.OrderDetailRepository;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {
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

	private Book book;
	private Member member;
	private Review review;
	private ReviewRequest reviewRequest;
	private ReviewImageResponse reviewImageResponse;
	private OrderDetail orderDetail;

	@BeforeEach
	public void setUp() {
		member = TestUtils.createMember(TestUtils.createCustomer(), TestUtils.creategrade());
		TestUtils.setIdForEntity(member, 1L);

		this.book = TestUtils.createBook(TestUtils.createPublisher());

		TestUtils.setIdForEntity(book, 1L);

		Order order = TestUtils.createOrder(member.getCustomer());

		this.orderDetail = TestUtils.createOrderDetail(order, book);
		TestUtils.setIdForEntity(orderDetail, 1l);

		reviewRequest = new ReviewRequest(
			"title",
			"content",
			4,
			orderDetail.getId(),
			List.of("http://example.com/image1.jpg", "http://example.com/image2.jpg")
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
	public void registerMemberNotFound() {
		when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());
		MemberIdContext.setMemberId(member.getId());
		assertThrows(MemberNotFoundException.class,
			() -> reviewService.registerReview(reviewRequest));
	}

	@Test
	public void registerOrderDetailNotFound() {
		when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
		when(orderDetailRepository.findById(anyLong())).thenReturn(Optional.empty());
		MemberIdContext.setMemberId(member.getId());
		assertThrows(OrderDetailNotFoundException.class,
			() -> reviewService.registerReview(reviewRequest));
	}

	@Test
	public void registerSuccess() {
		when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
		when(reviewRepository.save(any())).thenReturn(review);
		when(orderDetailRepository.findById(anyLong())).thenReturn(Optional.of(orderDetail));
		MemberIdContext.setMemberId(member.getId());
		assertEquals(ReviewMemberResponse.of(review),
			reviewService.registerReview(reviewRequest));
	}

	@Test
	public void registerByNullUser() {
		MemberIdContext.setMemberId(null);
		assertThrows(RequiredHeaderIsNullException.class,
			() -> reviewService.registerReview(reviewRequest));
	}

	@Test
	public void updateFailed() {
		when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());
		MemberIdContext.setMemberId(member.getId());
		assertThrows(ReviewNotFoundException.class,
			() -> reviewService.updateReview(reviewRequest, review.getId()));
	}

	@Test
	public void updatedByOtherUser() {
		when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));
		MemberIdContext.setMemberId(member.getId() + 10);
		assertThrows(ReviewNotFoundException.class,
			() -> reviewService.updateReview(reviewRequest, review.getId()));
	}

	@Test
	public void updatedByNullUser() {
		MemberIdContext.setMemberId(null);
		assertThrows(RequiredHeaderIsNullException.class,
			() -> reviewService.updateReview(reviewRequest, review.getId()));
	}

	@Test
	public void updateSuccess() {
		when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));
		MemberIdContext.setMemberId(member.getId());
		assertEquals(ReviewMemberResponse.of(review),
			reviewService.updateReview(reviewRequest, review.getId()));
	}

	@Test
	public void getScoreFail() {
		when(bookRepository.existsById(anyLong())).thenReturn(false);
		assertThrows(BookIdNotFoundException.class,
			() -> this.reviewService.getScoreByBookId(1));
	}

	@Test
	public void getScoreSuccess() {
		double score = 4.1;
		when(bookRepository.existsById(anyLong())).thenReturn(true);
		when(reviewRepository.findScoreByBookId(1)).thenReturn(score);
		assertEquals(reviewService.getScoreByBookId(1), score);
	}

	@Test
	public void getReviewsAndMemFail() {
		when(bookRepository.existsById(anyLong())).thenReturn(false);
		assertThrows(BookIdNotFoundException.class,
			() -> this.reviewService.getReviewsByBookId(1, PageRequest.of(0, 1)));
	}

	@Test
	public void getReviewsAndMemSuccess() {
		List<ReviewMemberResponse> res = new LinkedList<>();
		when(bookRepository.existsById(anyLong())).thenReturn(true);
		when(reviewRepository.findReviewsByBookId(anyLong(), any())).thenReturn(res);
		Page<ReviewMemberResponse> pageRes = reviewService.getReviewsByBookId(1, PageRequest.of(0, 1));
		assertEquals(0, pageRes.getContent().size());
		assertEquals(0, pageRes.getPageable().getPageNumber());
		assertEquals(1, pageRes.getPageable().getPageSize());
	}

	@Test
	public void getReviewsAndMemFullSuccess() {
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
	public void getReviewsAndBookFail() {
		when(memberRepository.existsById(anyLong())).thenReturn(false);
		assertThrows(MemberNotFoundException.class,
			() -> this.reviewService.getReviewsByMemberId(1, PageRequest.of(0, 2)));
	}

	@Test
	public void getReviewsAndBookSuccess() {
		List<ReviewBookResponse> res = new LinkedList<>();
		when(memberRepository.existsById(anyLong())).thenReturn(true);
		when(reviewRepository.findReviewsByMemberId(anyLong(), any())).thenReturn(res);
		PagedResponse<ReviewBookResponse> pageRes = reviewService.getReviewsByMemberId(1, PageRequest.of(0, 1));
		assertEquals(0, pageRes.content().size());
		assertEquals(0, pageRes.page());
		assertEquals(1, pageRes.size());
	}

	@Test
	public void getReviewsAndBookFullSuccess() {
		List<ReviewBookResponse> res = List.of(ReviewBookResponse.of(review));
		when(memberRepository.existsById(anyLong())).thenReturn(true);
		when(reviewRepository.findReviewsByMemberId(anyLong(), any())).thenReturn(res);
		List<ReviewImageDto> reviewImages = List.of(new ReviewImageDto(1L, reviewImageResponse));
		when(reviewImageRepository.findReviewImagesByReviewIds(anyList())).thenReturn(reviewImages);
		PagedResponse<ReviewBookResponse> pageRes = reviewService.getReviewsByMemberId(1, PageRequest.of(0, 1));
		assertEquals(1, pageRes.content().size());
		assertEquals(0, pageRes.page());
		assertEquals(1, pageRes.size());
	}
}
