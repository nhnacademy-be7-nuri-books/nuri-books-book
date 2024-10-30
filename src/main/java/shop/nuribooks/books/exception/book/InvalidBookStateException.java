package shop.nuribooks.books.exception.book;

import shop.nuribooks.books.exception.BadRequestException;

public class InvalidBookStateException extends BadRequestException {
	public InvalidBookStateException(String name) {
		super("잘못된 도서 상태" + name);
	}
}
