package shop.nuribooks.books.exception.book;

import shop.nuribooks.books.exception.DuplicateException;

public class DuplicateIsbnException extends DuplicateException {
	public DuplicateIsbnException(String isbn) {
		super("입력한 isbn " + isbn + " 은 이미 존재합니다.");
	}
}
