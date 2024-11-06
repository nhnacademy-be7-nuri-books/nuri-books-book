package shop.nuribooks.books.exception.review;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class ReviewNotFoundException extends ResourceNotFoundException {
	public ReviewNotFoundException() {
		super("리뷰를 찾을 수 없습니다.");
	}
}
