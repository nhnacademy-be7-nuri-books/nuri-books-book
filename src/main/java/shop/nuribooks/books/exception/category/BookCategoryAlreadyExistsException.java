package shop.nuribooks.books.exception.category;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;

public class BookCategoryAlreadyExistsException extends ResourceAlreadyExistException {
	public BookCategoryAlreadyExistsException(Long bookId, Long categoryId) {
		super("북카테고리가 이미 존재합니다. bookId : " + bookId + ", categoryID : " + categoryId);
	}
}
