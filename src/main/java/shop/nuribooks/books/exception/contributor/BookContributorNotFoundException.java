package shop.nuribooks.books.exception.contributor;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class BookContributorNotFoundException extends ResourceNotFoundException {
	public BookContributorNotFoundException(String message) {
		super(message);
	}
}
