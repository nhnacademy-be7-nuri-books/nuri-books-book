package shop.nuribooks.books.book.book.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.book.book.dto.response.TopBookResponse;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.entity.BookStateEnum;
import shop.nuribooks.books.book.publisher.entity.Publisher;
import shop.nuribooks.books.book.publisher.repository.PublisherRepository;
import shop.nuribooks.books.common.config.QuerydslConfiguration;

@Slf4j
@DataJpaTest
@Import(QuerydslConfiguration.class)
class BookRepositoryTest {

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private PublisherRepository publisherRepository;

	private Publisher publisher;

	private Book book;

	@BeforeEach
	void setUp() {
		publisher = publisherRepository.save(new Publisher("Publisher Name"));

		book = Book.builder()
			.state(BookStateEnum.OUT_OF_PRINT)
			.publisherId(publisher)
			.title("Test Book Title")
			.thumbnailImageUrl("thumbnail.jpg")
			.publicationDate(LocalDate.now())
			.price(BigDecimal.valueOf(10000))
			.discountRate(10)
			.description("Test Book Description")
			.contents("Test Book Contents")
			.isbn("1234567890123")
			.isPackageable(true)
			.likeCount(0)
			.stock(100)
			.viewCount(0L)
			.build();

		Book book1 = Book.builder()
			.state(BookStateEnum.OUT_OF_PRINT)
			.publisherId(publisher)
			.title("Aest Book Title")
			.thumbnailImageUrl("thumbnail.jpg")
			.publicationDate(LocalDate.now())
			.price(BigDecimal.valueOf(10000))
			.discountRate(10)
			.description("Test Book Description")
			.contents("Test Book Contents")
			.isbn("1234567890123")
			.isPackageable(true)
			.likeCount(50)
			.stock(100)
			.viewCount(0L)
			.build();

		bookRepository.save(book);
		bookRepository.save(book1);
	}

	@Test
	void ExistsByIsbnTrueWhenBookExists() {
		String isbn = "1234567890123";

		boolean exists = bookRepository.existsByIsbn(isbn);

		assertThat(exists).isTrue();
	}

	@Test
	void ExistsByIsbnFalseWhenBookNotExist() {
		String isbn = "9999999999999";

		boolean exists = bookRepository.existsByIsbn(isbn);

		assertThat(exists).isFalse();
	}

	@Test
	void testBookListAsc() {
		assertEquals(2,
			this.bookRepository.findAllWithPublisher(PageRequest.of(0, 5, Sort.by(Sort.Order.asc("title")))).size());
	}

	@Test
	void testBookListDesc() {
		assertEquals(2,
			this.bookRepository.findAllWithPublisher(PageRequest.of(0, 5, Sort.by(Sort.Order.desc("title")))).size());
	}

	@Test
	void testCount() {
		assertEquals(2, this.bookRepository.countBook());
	}

	@Test
	void testFindBookById() {
		assertEquals(book, this.bookRepository.findBookByIdAndDeletedAtIsNull(book.getId()).get());
	}

	@Test
	void testFindTopBookByLikes() {
		List<TopBookResponse> topBooks = bookRepository.findTopBooksByLikes();

		assertThat(topBooks).isNotEmpty();
		assertEquals(2, topBooks.size());
		assertEquals("Aest Book Title", topBooks.getFirst().title());
	}

	@Test
	void findTopBooksByScore() {
		List<TopBookResponse> topBooks = bookRepository.findTopBooksByScore();

		assertThat(topBooks).isNotEmpty();
		assertEquals(2, topBooks.size());
	}

	@Test
	void findAllAndDeletedAtIsNull() {
		List<Book> books = bookRepository.findAllAndDeletedAtIsNull();

		assertThat(books).isNotEmpty();
		assertEquals(2, books.size());
	}
}
