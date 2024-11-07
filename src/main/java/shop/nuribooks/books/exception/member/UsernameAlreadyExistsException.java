package shop.nuribooks.books.exception.member;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;

public class UsernameAlreadyExistsException extends ResourceAlreadyExistException {
	public UsernameAlreadyExistsException(String message) {
		super(message);
	}
}
