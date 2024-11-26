package shop.nuribooks.books.exception.order;

import shop.nuribooks.books.exception.BadRequestException;

public class CustomerRefundException extends BadRequestException {
	public CustomerRefundException(String message) {
		super(message);
	}
}
