package shop.nuribooks.books.exception;

public class CouponAlreadExistsException extends ResourceAlreadyExistException {
	public CouponAlreadExistsException() {
		super("해당 쿠폰이 이미 존재합니다.");
	}
}
