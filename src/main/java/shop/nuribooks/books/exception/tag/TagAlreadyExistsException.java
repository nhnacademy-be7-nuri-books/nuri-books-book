package shop.nuribooks.books.exception.tag;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;

public class TagAlreadyExistsException extends ResourceAlreadyExistException {
	public TagAlreadyExistsException(String message) {
		super(message);
	}
}
