package shop.nuribooks.books.exception.tag;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;

public class TagAlreadyExistsException extends ResourceAlreadyExistException {
	public TagAlreadyExistsException() {
		super("태그가 이미 등록되어 있습니다.");
	}
}
