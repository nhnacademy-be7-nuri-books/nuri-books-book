package shop.nuribooks.books.exception.contributor;

import shop.nuribooks.books.exception.DuplicateException;

public class DuplicateEntityException extends DuplicateException {
	public DuplicateEntityException(String message) {
		super(message);
	}

}
