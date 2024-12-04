package shop.nuribooks.books.exception.coupon;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;

public class CouponAlreadyIssuedException extends ResourceAlreadyExistException {
	public CouponAlreadyIssuedException() {
		super("이미 발급받은 쿠폰입니다.");
	}
}
