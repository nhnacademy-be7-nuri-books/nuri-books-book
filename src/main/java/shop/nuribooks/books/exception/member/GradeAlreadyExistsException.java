package shop.nuribooks.books.exception.member;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;

public class GradeAlreadyExistsException extends ResourceAlreadyExistException {
	public GradeAlreadyExistsException(String message) {
		super(message);
	}
}
