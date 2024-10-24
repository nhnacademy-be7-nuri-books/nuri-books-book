package shop.nuribooks.books.exception.book;

import jakarta.validation.constraints.NotNull;
import shop.nuribooks.books.exception.ResourceNotFoundException;

public class BookStatesIdNotFoundException extends ResourceNotFoundException {
	public BookStatesIdNotFoundException(@NotNull Integer id) {
		super("해당하는 도서상태 ID " + id + " 를 찾을 수 없습니다.");
	}
}
