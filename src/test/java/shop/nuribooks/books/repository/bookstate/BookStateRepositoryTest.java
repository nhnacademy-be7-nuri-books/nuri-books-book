package shop.nuribooks.books.repository.bookstate;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import shop.nuribooks.books.entity.book.BookState;

@DataJpaTest
public class BookStateRepositoryTest {

	@Autowired
	private BookStateRepository bookStateRepository;

	@BeforeEach
	public void setUp() {
		BookState bookState = BookState.of("절판");
		bookStateRepository.save(bookState);
	}

	@Test
	public void testExistsBookStatesByDetail_ShouldReturnTrue_WhenDetailExists() {
		String detail = "절판";

		boolean exist = bookStateRepository.existsBookStatesByDetail(detail);

		assertThat(exist).isTrue();
	}

	@Test
	public void testExistsBookStatesByDetail_ShouldReturnFalse_WhenDetailDoesNotExist() {
		String detail = "재고있음";

		boolean exist = bookStateRepository.existsBookStatesByDetail(detail);

		assertThat(exist).isFalse();
	}
}
