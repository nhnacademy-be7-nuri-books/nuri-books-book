package shop.nuribooks.books.order.shipping.exception;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class ShippingNotFoundException extends ResourceNotFoundException {
	public ShippingNotFoundException() {
		super("배송 내역이 존재하지 않습니다.");
	}
}
