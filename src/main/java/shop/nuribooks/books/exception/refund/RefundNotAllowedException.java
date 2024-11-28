package shop.nuribooks.books.exception.refund;

import shop.nuribooks.books.exception.BadRequestException;

public class RefundNotAllowedException extends BadRequestException {
	public RefundNotAllowedException(String message) {
		super(message);
	}
}
