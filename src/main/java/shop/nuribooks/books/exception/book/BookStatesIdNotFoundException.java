package shop.nuribooks.books.exception.book;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class BookStatesIdNotFoundException extends ResourceNotFoundException {
	public BookStatesIdNotFoundException(Integer id) {
		super("해당하는 도서상태 ID " + id + " 를 찾을 수 없습니다.");
	}
}
