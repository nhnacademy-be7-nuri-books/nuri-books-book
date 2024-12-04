package shop.nuribooks.books.exception.coupon;

import shop.nuribooks.books.exception.BadRequestException;

public class InvalidCouponException extends BadRequestException {
	public InvalidCouponException(String message) {
		super(message);
	}

	public InvalidCouponException() {
		super("유효하지 않은 쿠폰 형식입니다. 요청을 확인해주세요.");
	}

}
