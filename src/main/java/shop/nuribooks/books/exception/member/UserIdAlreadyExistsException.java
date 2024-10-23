package shop.nuribooks.books.exception.member;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;

public class UserIdAlreadyExistsException extends ResourceAlreadyExistException {
	public UserIdAlreadyExistsException(String message) {
		super(message);
	}
}
