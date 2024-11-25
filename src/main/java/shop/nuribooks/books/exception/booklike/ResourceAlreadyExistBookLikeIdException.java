package shop.nuribooks.books.exception.booklike;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;

public class ResourceAlreadyExistBookLikeIdException extends ResourceAlreadyExistException {
	public ResourceAlreadyExistBookLikeIdException() {
		super("이미 좋아요를 누른 도서입니다.");
	}
}
