package shop.nuribooks.books.exception.books;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class BookStatesIdNotFoundException extends ResourceNotFoundException {
	public BookStatesIdNotFoundException(Long id) {
		super("해당하는 도서상태 ID " + id + " 를 찾을 수 없습니다.");
	}
}
