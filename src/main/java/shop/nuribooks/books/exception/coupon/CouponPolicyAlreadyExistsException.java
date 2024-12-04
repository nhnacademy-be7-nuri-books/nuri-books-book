package shop.nuribooks.books.exception.coupon;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;

public class CouponPolicyAlreadyExistsException extends ResourceAlreadyExistException {

	public CouponPolicyAlreadyExistsException( ) {
		super("이미 존재하는 쿠폰 정책입니다.");
	}
}
