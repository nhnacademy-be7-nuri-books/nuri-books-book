package shop.nuribooks.books.exception.order;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class OrderNotFoundException extends ResourceNotFoundException {
	public OrderNotFoundException(String message) {
		super(message);
	}
}
