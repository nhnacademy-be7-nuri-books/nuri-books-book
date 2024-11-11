package shop.nuribooks.books.book.point.exception;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class PointHistoryNotFoundException extends ResourceNotFoundException {
	public PointHistoryNotFoundException() {
		super("포인트 내역을 찾을 수 없습니다.");
	}
}
