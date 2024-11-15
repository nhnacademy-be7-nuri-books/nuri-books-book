package shop.nuribooks.books.exception.coupon;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;

public class CouponAlreadyExistsException extends ResourceAlreadyExistException {
	public CouponAlreadyExistsException() {
		super("해당 쿠폰이 이미 존재합니다.");
	}
}
