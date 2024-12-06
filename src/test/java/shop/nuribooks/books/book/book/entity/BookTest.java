package shop.nuribooks.books.book.book.entity;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import shop.nuribooks.books.book.book.dto.request.BookUpdateRequest;
import shop.nuribooks.books.book.publisher.entity.Publisher;
import shop.nuribooks.books.exception.book.InvalidStockException;

class BookTest {

	@Test
	void testBookBuilder() {
		Publisher publisher = Publisher.builder().name("출판사 이름").build();
		Book book = Book.builder()
			.state(BookStateEnum.NORMAL)
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

		assertNotNull(book);
		assertEquals("테스트 책 제목", book.getTitle());
		assertEquals("thumbnail.jpg", book.getThumbnailImageUrl());
		assertEquals(10, book.getDiscountRate());
		assertEquals(100, book.getStock());
		assertTrue(book.isPackageable());
	}

	@Test
	void testBuilderWithNullValues() {
		Book book = Book.builder()
			.publisherId(null)
			.state(null)
			.title(null)
			.thumbnailImageUrl(null)
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
	void testIncrementViewCount() {
		Book book = Book.builder()
			.state(BookStateEnum.NORMAL)
			.publisherId(new Publisher("Publisher"))
			.title("Title")
			.viewCount(0L)
			.build();

		book.incrementViewCount();
		assertThat(book.getViewCount()).isEqualTo(1L);

		book.incrementViewCount();
		assertThat(book.getViewCount()).isEqualTo(2L);
	}

	@Test
	void testUpdateBookDetails() {
		Book book = Book.builder()
			.state(BookStateEnum.NORMAL)
			.price(BigDecimal.valueOf(15000))
			.discountRate(10)
			.stock(100)
			.thumbnailImageUrl("oldThumbnail.jpg")
			.description("Old Description")
			.contents("Old Contents")
			.isPackageable(false)
			.build();

		List<Long> tagIds = Arrays.asList(1L, 2L);
		List<Long> categoryIds = Arrays.asList(1L, 2L);

		BookUpdateRequest request = new BookUpdateRequest(
			BigDecimal.valueOf(20000),
			20,
			50,
			"정상판매",
			"newThumbnail.jpg",
			"New Description",
			"New Contents",
			true,
			tagIds,
			categoryIds
		);

		book.updateBookDetails(request);

		assertEquals(BigDecimal.valueOf(20000), book.getPrice());
		assertEquals(20, book.getDiscountRate());
		assertEquals(50, book.getStock());
		assertEquals(BookStateEnum.NORMAL, book.getState());
		assertEquals("newThumbnail.jpg", book.getThumbnailImageUrl());
		assertEquals("New Description", book.getDescription());
		assertEquals("New Contents", book.getContents());
		assertTrue(book.isPackageable());
	}

	@Test
	void testDelete() {
		Book book = Book.builder().build();
		assertNull(book.getDeletedAt());

		book.delete();
		assertNotNull(book.getDeletedAt());
		assertTrue(book.getDeletedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
	}

	@Test
	void testUpdateStock_ValidDecrease() {
		Book book = Book.builder()
			.stock(100)
			.state(BookStateEnum.NORMAL)
			.build();

		book.updateStock(50);
		assertEquals(50, book.getStock());
		assertEquals(BookStateEnum.NORMAL, book.getState());
	}

	@Test
	void testUpdateStock_ToZero() {
		Book book = Book.builder()
			.stock(10)
			.state(BookStateEnum.NORMAL)
			.build();

		book.updateStock(10);
		assertEquals(0, book.getStock());
		assertEquals(BookStateEnum.SOLD_OUT, book.getState());
	}

	@Test
	void testUpdateStock_InvalidDecrease() {
		Book book = Book.builder().stock(10).build();

		assertThrows(InvalidStockException.class, () -> book.updateStock(20));
		assertEquals(10, book.getStock());
	}

	@Test
	void testIncrementLikeCount() {
		Book book = Book.builder().likeCount(0).build();

		book.incrementLikeCount();
		assertEquals(1, book.getLikeCount());

		book.incrementLikeCount();
		assertEquals(2, book.getLikeCount());
	}

	@Test
	void testDecrementLikeCount() {
		Book book = Book.builder().likeCount(2).build();

		book.decrementLikeCount();
		assertEquals(1, book.getLikeCount());

		book.decrementLikeCount();
		assertEquals(0, book.getLikeCount());
	}
}