package shop.nuribooks.books.exception.book;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class BookNotFoundException extends ResourceNotFoundException {
	public BookNotFoundException(Long id) {
		super("해당 도서를 찾을 수 없습니다. Id : " + id);
	}
}
