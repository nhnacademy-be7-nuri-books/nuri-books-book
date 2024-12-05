package shop.nuribooks.books.book.book.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import shop.nuribooks.books.exception.book.InvalidBookStateException;

class BookStateEnumTest {

	@Test
	void fromString() {
		assertEquals(BookStateEnum.NEW, BookStateEnum.fromString("new"));
		assertEquals(BookStateEnum.NORMAL, BookStateEnum.fromString("NORMAL"));
	}

	@Test
	void fromStringThrowsInvalidBookStateException() {
		String invalidState = "invalid_state";

		assertThrows(InvalidBookStateException.class, () -> BookStateEnum.fromString(invalidState));
	}

	@Test
	void fromStringKor() {
		assertEquals(BookStateEnum.NEW, BookStateEnum.fromStringKor("신간"));
		assertEquals(BookStateEnum.NORMAL, BookStateEnum.fromStringKor("정상판매"));
		assertEquals(BookStateEnum.SOLD_OUT, BookStateEnum.fromStringKor("품절"));
	}

	@Test
	void fromStringKorIgnoreCase() {
		assertEquals(BookStateEnum.NEW, BookStateEnum.fromStringKor("new"));
		assertEquals(BookStateEnum.NORMAL, BookStateEnum.fromStringKor("NORMAL"));
		assertEquals(BookStateEnum.SOLD_OUT, BookStateEnum.fromStringKor("SOLD_OUT"));
	}

	@Test
	void fromStringKorThrowsInvalidBookStateException() {
		String invalidState = "잘못된 상태";

		assertThrows(InvalidBookStateException.class, () -> BookStateEnum.fromStringKor(invalidState));
	}
}