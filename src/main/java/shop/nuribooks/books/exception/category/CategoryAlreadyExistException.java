package shop.nuribooks.books.exception.category;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;

public class CategoryAlreadyExistException extends ResourceAlreadyExistException {
	public CategoryAlreadyExistException(String message) {
		super("카테고리 이름 '" + message + "' 가 이미 존재합니다");
	}

	public CategoryAlreadyExistException(Long id) {
		super("입력한 카테고리ID는 " + id + " 이미 존재합니다");
	}

}
