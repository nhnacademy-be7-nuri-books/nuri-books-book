package shop.nuribooks.books.exception.payment;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class PaymentNotFoundException extends ResourceNotFoundException {
	public PaymentNotFoundException(String message) {
		super(message);
	}
}
