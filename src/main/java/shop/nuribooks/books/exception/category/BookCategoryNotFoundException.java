package shop.nuribooks.books.exception.category;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class BookCategoryNotFoundException extends ResourceNotFoundException {
	public BookCategoryNotFoundException(Long bookId, Long categoryId) {
		super("");
	}
}
