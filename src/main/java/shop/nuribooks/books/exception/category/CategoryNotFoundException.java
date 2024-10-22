package shop.nuribooks.books.exception.category;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class CategoryNotFoundException extends ResourceNotFoundException {
	public CategoryNotFoundException(String message) {
		super(message);
	}
}
