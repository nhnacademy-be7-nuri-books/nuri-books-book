package shop.nuribooks.books.exception.common;

import shop.nuribooks.books.exception.BadRequestException;

public class RequiredHeaderIsNullException extends BadRequestException {
	public RequiredHeaderIsNullException() {
		super("필수 헤더가 비어있습니다.");
	}
}
