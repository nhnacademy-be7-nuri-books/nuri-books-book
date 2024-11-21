package shop.nuribooks.books.exception.order;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class OrderDetailNotFoundException extends ResourceNotFoundException {
	public OrderDetailNotFoundException(String message) {
		super(message);
	}
}
