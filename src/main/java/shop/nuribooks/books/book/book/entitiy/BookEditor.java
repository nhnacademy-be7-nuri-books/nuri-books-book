package shop.nuribooks.books.book.book.entitiy;

import lombok.Builder;
import lombok.Getter;
import shop.nuribooks.books.book.bookstate.entitiy.BookState;

@Getter
public class BookEditor {
	private final BookState stateId;

	@Builder
	public BookEditor(BookState stateId) {
		this.stateId = stateId;
	}
}
