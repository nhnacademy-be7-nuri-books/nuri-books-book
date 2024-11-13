package shop.nuribooks.books.book.point.exception;

import shop.nuribooks.books.exception.BadRequestException;

public class PointUsingMuchMoreException extends BadRequestException {
	public PointUsingMuchMoreException() {
		super("포인트가 부족합니다.");
	}
}
