package shop.nuribooks.books.exception.bookstate;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class BookStateIdNotFoundException extends ResourceNotFoundException {
	public BookStateIdNotFoundException() {
		super("도서상태를 찾을 수 없습니다.");
	}
}
