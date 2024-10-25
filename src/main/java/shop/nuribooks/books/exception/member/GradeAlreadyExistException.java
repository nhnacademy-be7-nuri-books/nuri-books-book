package shop.nuribooks.books.exception.member;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;

public class GradeAlreadyExistException extends ResourceAlreadyExistException {
	public GradeAlreadyExistException(String message) {
		super(message);
	}
}
