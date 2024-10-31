package shop.nuribooks.books.exception.contributor;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class ContributorNotFoundException extends ResourceNotFoundException {
	public ContributorNotFoundException(String message) {
		super(message);
	}

}
