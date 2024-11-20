package shop.nuribooks.books.exception.coupon;

public class MemberCouponAlreadyUsedException extends RuntimeException {
	public MemberCouponAlreadyUsedException(String message) {
		super(message);
	}
}
