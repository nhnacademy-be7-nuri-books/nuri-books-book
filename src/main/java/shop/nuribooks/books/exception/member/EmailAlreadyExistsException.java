package shop.nuribooks.books.exception.member;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;

public class EmailAlreadyExistsException extends ResourceAlreadyExistException {
	public EmailAlreadyExistsException(String message) {
		super(message);
	}
}
