package shop.nuribooks.books.exception.member;

import shop.nuribooks.books.exception.DuplicateException;

public class UserIdAlreadyExistsException extends DuplicateException {
	public UserIdAlreadyExistsException(String message) {
		super(message);
	}
}
