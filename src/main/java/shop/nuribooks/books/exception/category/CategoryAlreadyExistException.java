package shop.nuribooks.books.exception.category;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;

public class CategoryAlreadyExistException extends ResourceAlreadyExistException {
	public CategoryAlreadyExistException(String message) {
		super(message);
	}
}
