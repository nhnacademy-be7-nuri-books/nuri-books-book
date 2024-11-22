package shop.nuribooks.books.exception.tag;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;

public class BookTagAlreadyExistsException extends ResourceAlreadyExistException {
	public BookTagAlreadyExistsException() {
		super("해당 도서에 이미 등록된 태그입니다.");
	}
}
