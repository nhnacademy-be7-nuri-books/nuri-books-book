package shop.nuribooks.books.book.book.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.bookstate.entitiy.BookState;
import shop.nuribooks.books.book.publisher.entitiy.Publisher;
import shop.nuribooks.books.book.bookstate.repository.BookStateRepository;
import shop.nuribooks.books.book.publisher.repository.PublisherRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BookRepositoryTest {

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private BookStateRepository bookStateRepository;

	@Autowired
	private PublisherRepository publisherRepository;

	@BeforeEach
	public void setUp() {
		BookState bookState = BookState.builder().detail("절판").build();
		bookStateRepository.save(bookState);

		Publisher publisher = publisherRepository.save(new Publisher("Publisher Name"));

		Book book = Book.builder()
			.stateId(bookState)
			.publisherId(publisher)
			.title("Test Book Title")
			.thumbnailImageUrl("thumbnail.jpg")
			.detailImageUrl("detail.jpg")
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

		bookRepository.save(book);
	}


	@Test
	public void testExistsByIsbn_ReturnsTrueWhenBookExists() {
		String isbn = "1234567890123";

		boolean exists = bookRepository.existsByIsbn(isbn);

		assertThat(exists).isTrue();
	}

	@Test
	public void testExistsByIsbn_ReturnsFalseWhenBookDoesNotExist() {
		String isbn = "9999999999999";

		boolean exists = bookRepository.existsByIsbn(isbn);

		assertThat(exists).isFalse();
	}
}
