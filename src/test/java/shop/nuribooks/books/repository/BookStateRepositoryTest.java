package shop.nuribooks.books.repository;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import shop.nuribooks.books.entity.book.BookStates;
import shop.nuribooks.books.entity.book.enums.BookStatesEnum;
import shop.nuribooks.books.repository.book.BookStateRepository;

@DataJpaTest
public class BookStateRepositoryTest {

	@Autowired
	private BookStateRepository bookStateRepository;

	@BeforeEach
	public void setUp() {
		BookStates bookState1 = new BookStates();
		bookState1.setDetail(BookStatesEnum.DISCONTINUED);
		bookStateRepository.save(bookState1);
	}

	@Test
	public void testExistsBookStatesByDetail_ShouldReturnTrue_WhenDetailExists() {
		BookStatesEnum detail = BookStatesEnum.DISCONTINUED;

		boolean exists = bookStateRepository.existsBookStatesByDetail(detail);

		assertThat(exists).isTrue();
	}

	@Test
	public void testExistsBookStatesByDetail_ShouldReturnFalse_WhenDetailDoesNotExist() {
		BookStatesEnum detail = BookStatesEnum.INSTOCK;

		boolean exists = bookStateRepository.existsBookStatesByDetail(detail);

		assertThat(exists).isFalse();
	}

}
