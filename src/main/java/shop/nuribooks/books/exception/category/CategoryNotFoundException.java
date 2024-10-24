package shop.nuribooks.books.exception.category;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class CategoryNotFoundException extends ResourceNotFoundException {
	public CategoryNotFoundException(String message) {
		super(message);
	}

	public CategoryNotFoundException(Long id) {
		super("입력한 카테고리ID는 " + id + " 존재하지 않습니다.");
	}

}
