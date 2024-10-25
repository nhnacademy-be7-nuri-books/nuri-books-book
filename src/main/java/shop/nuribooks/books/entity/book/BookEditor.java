package shop.nuribooks.books.entity.book;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BookEditor {
	private final BookState stateId;

	@Builder
	public BookEditor(BookState stateId) {
		this.stateId = stateId;
	}
}
