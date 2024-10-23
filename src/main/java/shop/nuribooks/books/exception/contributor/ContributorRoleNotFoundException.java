package shop.nuribooks.books.exception.contributor;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class ContributorRoleNotFoundException extends ResourceNotFoundException {
	public ContributorRoleNotFoundException(String message) {
		super(message);
	}

}
