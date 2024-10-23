package shop.nuribooks.books.exception.member;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class CustomerNotFoundException extends ResourceNotFoundException {
	public CustomerNotFoundException(String message) {
		super(message);
	}
}
