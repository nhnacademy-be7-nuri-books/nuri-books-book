package shop.nuribooks.books.exception.contributor;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;

public class DuplicateEntityException extends ResourceAlreadyExistException {
	public DuplicateEntityException(String message) {
		super(message);
	}

}
