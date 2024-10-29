package shop.nuribooks.books.exception.tag;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class TagNotFoundException extends ResourceNotFoundException {
	public TagNotFoundException(String message) {
		super(message);
	}
}
