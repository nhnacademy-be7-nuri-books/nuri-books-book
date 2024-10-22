package shop.nuribooks.books.exception.book;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;

public class ResourceAlreadyExistIsbnException extends ResourceAlreadyExistException {
	public ResourceAlreadyExistIsbnException(String isbn) {
		super("입력한 isbn " + isbn + " 은 이미 존재합니다.");
	}
}
