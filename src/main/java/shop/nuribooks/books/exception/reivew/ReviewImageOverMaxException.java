package shop.nuribooks.books.exception.reivew;

import shop.nuribooks.books.exception.BadRequestException;

public class ReviewImageOverMaxException extends BadRequestException {
	public ReviewImageOverMaxException() {
		super("최대 리뷰 이미지 개수는 10개입니다.");
	}
}
