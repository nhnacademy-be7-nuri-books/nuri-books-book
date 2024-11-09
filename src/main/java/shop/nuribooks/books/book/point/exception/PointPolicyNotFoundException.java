package shop.nuribooks.books.book.point.exception;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class PointPolicyNotFoundException extends ResourceNotFoundException {
	public PointPolicyNotFoundException() {
		super("포인트 정책을 찾을 수 없습니다.");
	}
}
