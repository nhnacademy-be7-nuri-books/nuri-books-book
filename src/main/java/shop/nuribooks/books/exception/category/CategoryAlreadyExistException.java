package shop.nuribooks.books.exception.category;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;

public class CategoryAlreadyExistException extends ResourceAlreadyExistException {
	public CategoryAlreadyExistException(String message) {
		super(message);
	}

	public CategoryAlreadyExistException(Long id) {
		super("입력한 카테고리ID는 " + id + " 이미 존재합니다");
	}

}
