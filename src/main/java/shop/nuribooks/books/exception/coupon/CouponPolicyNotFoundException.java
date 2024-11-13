package shop.nuribooks.books.exception.coupon;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class CouponPolicyNotFoundException extends ResourceNotFoundException {
	public CouponPolicyNotFoundException(String message) {
		super(message);
	}
}
