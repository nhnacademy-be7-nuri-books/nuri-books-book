package shop.nuribooks.books.exception.order.detail;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;

public class OrderNotBelongsToUserException extends ResourceAlreadyExistException {
	public OrderNotBelongsToUserException(String string) {
		super(string);
	}
}
