package shop.nuribooks.books.exception.tag;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class BookTagNotFountException extends ResourceNotFoundException {
	public BookTagNotFountException() {
		super("도서태그가 존재하지 않습니다.");
	}
}
