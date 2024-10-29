package shop.nuribooks.books.exception.member;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class GradeNotFoundException extends ResourceNotFoundException {
	public GradeNotFoundException(String message) {
		super(message);
	}
}
