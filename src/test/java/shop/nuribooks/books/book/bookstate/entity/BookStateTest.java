package shop.nuribooks.books.book.bookstate.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import shop.nuribooks.books.book.bookstate.entitiy.BookState;

public class BookStateTest {

	@Test
	public void testBuilderWithNoFields() {
		BookState bookState = BookState.builder().build();
		assertThat(bookState).isNotNull();
		assertThat(bookState.getDetail()).isNull();
	}

	@Test
	public void testBuilderCreatesBookStateWithGivenDetail() {
		BookState bookState = BookState.builder().detail("재고있음").build();

		assertThat(bookState).isNotNull();
		assertThat(bookState.getDetail()).isEqualTo("재고있음");
	}

	@Test
	public void testGetDetail() {
		BookState bookState = BookState.builder().detail("재고있음").build();
		assertThat(bookState.getDetail()).isEqualTo("재고있음");
	}


	@Test
	public void testUpdateDetail() {
		BookState bookState = BookState.builder().detail("재고있음").build();
		bookState.updateDetail("매진");

		assertThat(bookState.getDetail()).isEqualTo("매진");
	}
}
