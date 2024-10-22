package shop.nuribooks.books.exception.address;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class AddressNotFoundException extends ResourceNotFoundException {
	public AddressNotFoundException(String message) {
		super(message);
	}
}
