package shop.nuribooks.books.exception.book;

import shop.nuribooks.books.exception.IsAlreadyExistsException;

public class IsAlreadyExistsIsbnException extends IsAlreadyExistsException {
	public IsAlreadyExistsIsbnException(String isbn) {
		super("입력한 isbn " + isbn + " 은 이미 존재합니다.");
	}
}
