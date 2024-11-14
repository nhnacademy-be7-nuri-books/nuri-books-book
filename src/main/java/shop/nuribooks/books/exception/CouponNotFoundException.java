package shop.nuribooks.books.exception;

public class CouponNotFoundException extends ResourceNotFoundException {
	public CouponNotFoundException() {
		super("쿠폰이 존재하지 않습니다.");
	}
}
