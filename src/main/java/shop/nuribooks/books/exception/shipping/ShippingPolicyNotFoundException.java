package shop.nuribooks.books.exception.shipping;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class ShippingPolicyNotFoundException extends ResourceNotFoundException {
	public ShippingPolicyNotFoundException() {
		super("배송비 정책이 존재하지 않습니다.");
	}
}