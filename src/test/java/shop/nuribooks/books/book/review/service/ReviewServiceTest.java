package shop.nuribooks.books.book.review.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import shop.nuribooks.books.common.message.PagedResponse;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;
import shop.nuribooks.books.exception.book.BookIdNotFoundException;
import shop.nuribooks.books.exception.common.RequiredHeaderIsNullException;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.exception.review.ReviewNotFoundException;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;

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

	private Book book;
	private Member member;
	private Review review;
	private ReviewRequest reviewRequest;
	private ReviewImageResponse reviewImageResponse;

	@BeforeEach
	public void setUp() {
		this.book = Book.builder()
			.title("Original Book Title")
			.thumbnailImageUrl("original_thumbnail.jpg")
			.detailImageUrl("original_detail.jpg")
			.publicationDate(LocalDate.now())
			.price(BigDecimal.valueOf(20000))
			.discountRate(10)
			.description("Original Description")
			.contents("Original Contents")
			.isbn("1234567890123")
			.isPackageable(true)
			.stock(100)
			.likeCount(0)
			.viewCount(0L)
			.build();

		ReflectionTestUtils.setField(book, "id", 1L);

		reviewRequest = new ReviewRequest(
			"title",
			"content",
			4,
			book.getId(),
			List.of("http://example.com/image1.jpg", "http://example.com/image2.jpg")
		);

		member = Member.builder()
			.username("nuribooks95")
			.birthday(LocalDate.of(1988, 8, 12))
			.build();
		ReflectionTestUtils.setField(member, "id", 1L);

		review = Review.builder()
			.title("title")
			.content("content")
			.score(4)
			.member(member)
			.book(book)
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
	public void registerBookNotFound() {
		when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
		when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());
		MemberIdContext.setMemberId(member.getId());
		assertThrows(BookIdNotFoundException.class,
			() -> reviewService.registerReview(reviewRequest));
	}

	@Test
	public void registerSuccess() {
		when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
		when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
		when(reviewRepository.save(any())).thenReturn(review);
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
		when(reviewRepository.save(any())).thenReturn(review);
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
		PagedResponse<ReviewMemberResponse> pageRes = reviewService.getReviewsByBookId(1, PageRequest.of(0, 1));
		assertEquals(0, pageRes.content().size());
		assertEquals(0, pageRes.page());
		assertEquals(1, pageRes.size());
	}

	@Test
	public void getReviewsAndMemFullSuccess() {
		List<ReviewMemberResponse> res = List.of(ReviewMemberResponse.of(review));
		when(bookRepository.existsById(anyLong())).thenReturn(true);
		when(reviewRepository.findReviewsByBookId(anyLong(), any())).thenReturn(res);
		List<ReviewImageDto> reviewImages = List.of(new ReviewImageDto(1L, reviewImageResponse));
		when(reviewImageRepository.findReviewImagesByReviewIds(anyList())).thenReturn(reviewImages);
		PagedResponse<ReviewMemberResponse> pageRes = reviewService.getReviewsByBookId(1, PageRequest.of(0, 1));
		assertEquals(1, pageRes.content().size());
		assertEquals(0, pageRes.page());
		assertEquals(1, pageRes.size());
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
