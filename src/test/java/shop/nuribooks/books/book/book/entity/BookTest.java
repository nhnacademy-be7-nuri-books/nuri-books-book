package shop.nuribooks.books.book.book.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import shop.nuribooks.books.book.book.dto.BookUpdateRequest;
import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.bookstate.entitiy.BookState;
import shop.nuribooks.books.book.publisher.entitiy.Publisher;

public class BookTest {

	@Test
	public void testBookBuilder() {
		Book book = Book.builder()
			.stateId(BookState.builder().detail("재고 있음").build())
			.publisherId(new Publisher("출판사 이름"))
			.title("테스트 책 제목")
			.thumbnailImageUrl("thumbnail.jpg")
			.detailImageUrl("detail.jpg")
			.publicationDate(LocalDate.now())
			.price(BigDecimal.valueOf(15000))
			.discountRate(10)
			.description("테스트 책 설명")
			.contents("테스트 책 내용")
			.isbn("1234567890123")
			.isPackageable(true)
			.stock(100)
			.likeCount(0)
			.viewCount(0L)
			.build();

		assertNotNull(book);
		assertEquals("테스트 책 제목", book.getTitle());
		assertEquals("thumbnail.jpg", book.getThumbnailImageUrl());
		assertEquals(10, book.getDiscountRate());
		assertEquals(100, book.getStock());
		assertTrue(book.isPackageable());
	}

	@Test
	public void testUpdateBookDetails() {
		BookState state = BookState.builder().detail("재고있음").build();
		Publisher publisher = Publisher.builder().name("Publisher").build();
		Book book = Book.builder()
			.stateId(state)
			.publisherId(publisher)
			.title("Book Title")
			.thumbnailImageUrl("thumbnail.jpg")
			.publicationDate(LocalDate.now())
			.price(BigDecimal.valueOf(10000))
			.discountRate(10)
			.description("Description")
			.contents("Contents")
			.isbn("1234567890")
			.stock(10)
			.likeCount(0)
			.viewCount(0L)
			.build();

		BookUpdateRequest request = new BookUpdateRequest(
			1,
			1L,
			"Updated Title",
			"updated_thumbnail.jpg",
			"updated_detail.jpg",
			LocalDate.parse("2025-01-01"),
			BigDecimal.valueOf(20000),
			5,
			"Updated Description",
			"Updated Contents",
			"0987654321",
			false,
			20
		);

		book.updateBookDetails(request, state, publisher);

		assertThat(book.getTitle()).isEqualTo("Updated Title");
		assertThat(book.getThumbnailImageUrl()).isEqualTo("updated_thumbnail.jpg");
		assertThat(book.getDescription()).isEqualTo("Updated Description");
	}

	@Test
	public void testBuilderWithNullValues() {
		Book book = Book.builder()
			.stateId(null)
			.publisherId(null)
			.title(null)
			.thumbnailImageUrl(null)
			.detailImageUrl(null)
			.publicationDate(null)
			.price(null)
			.discountRate(0)
			.description(null)
			.contents(null)
			.isbn(null)
			.isPackageable(false)
			.stock(0)
			.likeCount(0)
			.viewCount(0L)
			.build();

		assertNotNull(book);
		assertNull(book.getTitle());
		assertNull(book.getPublicationDate());
		assertNull(book.getPrice());
	}

	@Test
	public void testUpdateStateId() {
		BookState initialBookState = BookState.builder().detail("재고 있음").build();
		BookState newBookState = BookState.builder().detail("매진").build();
		Publisher publisher = Publisher.builder().name("출판사 이름").build();
		Book book = Book.builder()
			.stateId(initialBookState)
			.publisherId(publisher)
			.title("테스트 책 제목")
			.thumbnailImageUrl("thumbnail.jpg")
			.publicationDate(LocalDate.now())
			.price(BigDecimal.valueOf(15000))
			.discountRate(10)
			.description("테스트 책 설명")
			.contents("테스트 책 내용")
			.isbn("1234567890123")
			.isPackageable(true)
			.stock(100)
			.likeCount(0)
			.viewCount(0L)
			.build();

		book.updateStateId(newBookState);

		assertThat(book.getStateId()).isEqualTo(newBookState);
		assertThat(book.getStateId().getDetail()).isEqualTo("매진");
	}

}