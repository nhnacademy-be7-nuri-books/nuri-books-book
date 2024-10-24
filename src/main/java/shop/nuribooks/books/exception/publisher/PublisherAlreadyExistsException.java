package shop.nuribooks.books.exception.publisher;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;

public class PublisherAlreadyExistsException extends ResourceAlreadyExistException {
	public PublisherAlreadyExistsException(String message) {
		super(message);
	}
}
