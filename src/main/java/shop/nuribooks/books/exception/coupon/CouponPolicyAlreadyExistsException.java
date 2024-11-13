package shop.nuribooks.books.exception.coupon;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;

public class CouponPolicyAlreadyExistsException extends ResourceAlreadyExistException {
	public CouponPolicyAlreadyExistsException(String message) {
		super(message);
	}
}
