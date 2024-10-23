package shop.nuribooks.books.exception.member;

import shop.nuribooks.books.exception.BadRequestException;

public class InvalidPasswordException extends BadRequestException {
	public InvalidPasswordException(String message) {
		super(message);
	}
}
