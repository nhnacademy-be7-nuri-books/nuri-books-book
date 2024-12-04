package shop.nuribooks.books.exception.coupon;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class CouponPolicyNotFoundException extends ResourceNotFoundException {
	public CouponPolicyNotFoundException() {
		super("쿠폰 정책이 존재하지 않습니다.");
	}
}
