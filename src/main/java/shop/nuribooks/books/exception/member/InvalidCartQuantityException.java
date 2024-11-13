package shop.nuribooks.books.exception.member;

import shop.nuribooks.books.exception.BadRequestException;

public class InvalidCartQuantityException extends BadRequestException {
	public InvalidCartQuantityException(String message) {
		super(message);
	}
}
