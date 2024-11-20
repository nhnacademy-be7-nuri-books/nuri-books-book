package shop.nuribooks.books.exception.coupon;

public class MemberCouponExpiredException extends RuntimeException {
	public MemberCouponExpiredException(String message) {
		super(message);
	}
}
