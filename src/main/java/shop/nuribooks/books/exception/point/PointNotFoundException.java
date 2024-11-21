package shop.nuribooks.books.exception.point;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class PointNotFoundException extends ResourceNotFoundException {
	public PointNotFoundException() {
		super("포인트를 찾을 수 없습니다.");
	}
}
