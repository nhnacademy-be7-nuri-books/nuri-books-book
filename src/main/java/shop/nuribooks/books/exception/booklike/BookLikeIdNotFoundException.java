package shop.nuribooks.books.exception.booklike;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class BookLikeIdNotFoundException extends ResourceNotFoundException {
	public BookLikeIdNotFoundException() {
		super("좋아요가 존재하지 않습니다.");
	}
}
