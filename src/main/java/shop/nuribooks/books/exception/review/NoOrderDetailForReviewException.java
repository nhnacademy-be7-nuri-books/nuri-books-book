package shop.nuribooks.books.exception.review;

import shop.nuribooks.books.exception.BadRequestException;

public class NoOrderDetailForReviewException extends BadRequestException {
	public NoOrderDetailForReviewException() {
		super("주문 내역이 없거나 이미 리뷰가 작성되었습니다.");
	}
}
