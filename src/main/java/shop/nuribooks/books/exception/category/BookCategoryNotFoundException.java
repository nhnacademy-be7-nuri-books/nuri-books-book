package shop.nuribooks.books.exception.category;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class BookCategoryNotFoundException extends ResourceNotFoundException {
	public BookCategoryNotFoundException(Long bookId, Long categoryId) {
		super("북카테고리를 찾을 수 없습니다. bookId: " + bookId + ", categoryId: " + categoryId);
	}
}
