package shop.nuribooks.books.exception.member;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;

public class PhoneNumberAlreadyExistsException extends ResourceAlreadyExistException {
	public PhoneNumberAlreadyExistsException(String message) {
		super(message);
	}
}
