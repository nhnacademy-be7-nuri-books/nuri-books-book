package shop.nuribooks.books.order.shipping.exception;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class CanNotDeliveryException extends ResourceNotFoundException {
	public CanNotDeliveryException(String message) {
		super(message);
	}
}
