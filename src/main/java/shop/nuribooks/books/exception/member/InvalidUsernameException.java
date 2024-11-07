package shop.nuribooks.books.exception.member;

import shop.nuribooks.books.exception.BadRequestException;

public class InvalidUsernameException extends BadRequestException {
	public InvalidUsernameException(String message) {
		super(message);
	}
}
