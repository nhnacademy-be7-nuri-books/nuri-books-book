package shop.nuribooks.books.repository.bookstate;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import shop.nuribooks.books.entity.book.BookState;
import shop.nuribooks.books.entity.book.BookStateEnum;

@DataJpaTest
public class BookStateRepositoryTest {

	@Autowired
	private BookStateRepository bookStateRepository;

	@BeforeEach
	public void setUp() {
		BookState bookState = BookState.of(BookStateEnum.DISCONTINUED);
		bookStateRepository.save(bookState);
	}

	@Test
	public void testExistsBookStatesByDetail_ShouldReturnTrue_WhenDetailExists() {
		BookStateEnum detail = BookStateEnum.DISCONTINUED;

		boolean exist = bookStateRepository.existsBookStatesByDetail(detail);

		assertThat(exist).isTrue();
	}

	@Test
	public void testExistsBookStatesByDetail_ShouldReturnFalse_WhenDetailDoesNotExist() {
		BookStateEnum detail = BookStateEnum.INSTOCK;

		boolean exist = bookStateRepository.existsBookStatesByDetail(detail);

		assertThat(exist).isFalse();
	}
}
