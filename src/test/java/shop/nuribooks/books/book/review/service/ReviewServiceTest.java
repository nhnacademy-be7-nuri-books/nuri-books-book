package shop.nuribooks.books.book.review.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.review.dto.request.ReviewRequest;
import shop.nuribooks.books.book.review.dto.response.ReviewMemberResponse;
import shop.nuribooks.books.book.review.entity.Review;
import shop.nuribooks.books.book.review.repository.ReviewRepository;
import shop.nuribooks.books.book.review.service.impl.ReviewServiceImpl;
import shop.nuribooks.books.exception.book.BookIdNotFoundException;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
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

	private Book book;
	private Member member;
	private Review review;
	private ReviewRequest reviewRequest;

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
			.userId("nuribooks95")
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
	}

	@Test
	public void registerMemberNotFound() {
		when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());
		assertThrows(MemberNotFoundException.class,
			() -> reviewService.registerReview(reviewRequest, member.getId()));
	}

	@Test
	public void registerBookNotFound() {
		when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
		when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());
		assertThrows(BookIdNotFoundException.class,
			() -> reviewService.registerReview(reviewRequest, member.getId()));
	}

	@Test
	public void registerSuccess() {
		when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
		when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
		when(reviewRepository.save(any())).thenReturn(review);
		assertEquals(ReviewMemberResponse.of(review),
			reviewService.registerReview(reviewRequest, member.getId()));
	}
}
