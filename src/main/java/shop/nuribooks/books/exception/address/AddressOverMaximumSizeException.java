package shop.nuribooks.books.exception.address;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class AddressOverMaximumSizeException extends ResourceNotFoundException {
	public AddressOverMaximumSizeException(String message) {
		super(message);
	}
}
