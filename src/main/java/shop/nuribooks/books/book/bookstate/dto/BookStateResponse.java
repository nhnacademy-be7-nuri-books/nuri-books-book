package shop.nuribooks.books.book.bookstate.dto;

import shop.nuribooks.books.book.bookstate.entitiy.BookState;

public record BookStateResponse(Integer id, String detail) {
	public static BookStateResponse of(BookState bookState) {
		return new BookStateResponse(
			bookState.getId(),
			bookState.getDetail()
		);
	}
}
