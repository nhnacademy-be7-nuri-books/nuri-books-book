package shop.nuribooks.books.repository;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import shop.nuribooks.books.entity.book.BookStates;
import shop.nuribooks.books.entity.book.enums.BookStatesEnum;
import shop.nuribooks.books.entity.book.Book;
import shop.nuribooks.books.entity.Publishers;
import shop.nuribooks.books.repository.book.BookRepository;
import shop.nuribooks.books.repository.book.BookStateRepository;
import shop.nuribooks.books.repository.book.PublisherRepository;

@DataJpaTest
public class BookRepositoryTest {

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private BookStateRepository bookStateRepository;

	@Autowired
	private PublisherRepository publisherRepository;

	@Test
	@Transactional
	public void testExistsByIsbn(){
		BookStates bookState = new BookStates();
		bookState.setDetail(BookStatesEnum.INSTOCK);
		bookStateRepository.save(bookState);

		Publishers publisher = new Publishers();
		publisher.setName("TEST Publisher");
		publisherRepository.save(publisher);

		Book book = Book.builder()
			.stateId(bookState)
			.publisherId(publisher)
			.title("Test Book")
			.thumbnailImageUrl("http://example.com/thumbnail.jpg")
			.publicationDate(LocalDate.now())
			.price(BigDecimal.valueOf(19.99))
			.discountRate(10)
			.description("Test Description")
			.contents("Test Contents")
			.isbn("1234567890123")
			.isPackageable(false)
			.likeCount(0)
			.stock(100)
			.viewCount(0L)
			.build();

		bookRepository.save(book);

		boolean exists = bookRepository.existsByIsbn("1234567890123");

		Assertions.assertTrue(exists);
	}

	@Test
	@Transactional
	public void testDoesNotExistByIsbn() {
		boolean exists = bookRepository.existsByIsbn("non");

		Assertions.assertFalse(exists);
	}
}
