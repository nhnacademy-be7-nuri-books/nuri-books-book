package shop.nuribooks.books.exception.book;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class BookIdNotFoundException extends ResourceNotFoundException {
	public BookIdNotFoundException() {
		super("존재하지 않는 도서입니다.");
	}
}
