package shop.nuribooks.books.exception.member;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;

public class PasswordDuplicateException extends ResourceAlreadyExistException {
	public PasswordDuplicateException(String message) {
		super(message);
	}
}
