package shop.nuribooks.books.exception.coupon;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class CouponNotFoundException extends ResourceNotFoundException {
	public CouponNotFoundException(String message) {
		super(message);
	}
}

	public CouponNotFoundException() {
		super("쿠폰이 존재하지 않습니다.");
	}
}
