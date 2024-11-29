package shop.nuribooks.books.exception.payment;

import shop.nuribooks.books.exception.BadRequestException;

public class FailedPaymentCancelException extends BadRequestException {
	public FailedPaymentCancelException(String message) {
		super(message);
	}
}
