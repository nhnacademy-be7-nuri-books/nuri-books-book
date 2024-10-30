package shop.nuribooks.books.book.book.entity;

import org.junit.jupiter.api.Test;
import shop.nuribooks.books.book.book.entitiy.BookStateEnum;
import shop.nuribooks.books.exception.book.InvalidBookStateException;

import static org.junit.jupiter.api.Assertions.*;

class BookStateEnumTest {

	@Test
	void fromString_ShouldReturnEnum_WhenValidString() {
		assertEquals(BookStateEnum.NEW, BookStateEnum.fromString("new"));
		assertEquals(BookStateEnum.NORMAL, BookStateEnum.fromString("NORMAL"));
	}

	@Test
	void fromString_ShouldThrowInvalidBookStateException_WhenInvalidString() {
		String invalidState = "invalid_state";

		assertThrows(InvalidBookStateException.class, () -> BookStateEnum.fromString(invalidState));
	}
}