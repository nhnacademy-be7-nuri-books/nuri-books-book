package shop.nuribooks.books.exception.member;

import shop.nuribooks.books.exception.DuplicateException;

public class EmailAlreadyExistsException extends DuplicateException {
	public EmailAlreadyExistsException(String message) {
		super(message);
	}
}
