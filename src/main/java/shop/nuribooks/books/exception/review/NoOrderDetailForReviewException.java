package shop.nuribooks.books.exception.review;

import shop.nuribooks.books.exception.BadRequestException;

public class NoOrderDetailForReviewException extends BadRequestException {
	public NoOrderDetailForReviewException() {
		super("주문 상세가 없습니다.");
	}
}
