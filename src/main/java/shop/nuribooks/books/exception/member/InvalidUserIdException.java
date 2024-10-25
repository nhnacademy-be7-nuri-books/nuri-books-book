package shop.nuribooks.books.exception.member;

import shop.nuribooks.books.exception.BadRequestException;

public class InvalidUserIdException extends BadRequestException {
	public InvalidUserIdException(String message) {
		super(message);
	}
}
