package shop.nuribooks.books.exception.publisher;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class PublisherNotFoundException extends ResourceNotFoundException {
	public PublisherNotFoundException(String message) {
		super(message);
	}
}
