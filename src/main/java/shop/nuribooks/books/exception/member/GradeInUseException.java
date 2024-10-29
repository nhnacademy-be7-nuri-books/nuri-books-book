package shop.nuribooks.books.exception.member;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;

public class GradeInUseException extends ResourceAlreadyExistException {
	public GradeInUseException(String message) {
		super(message);
	}
}
