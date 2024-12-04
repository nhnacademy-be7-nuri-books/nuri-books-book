package shop.nuribooks.books.exception.coupon;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class BookCouponNotFoundException extends ResourceNotFoundException {
	public BookCouponNotFoundException() {
		super("해당하는 도서쿠폰이 존재하지 않습니다.");
	}
}
