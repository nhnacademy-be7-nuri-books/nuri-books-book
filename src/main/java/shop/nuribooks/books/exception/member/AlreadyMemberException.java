package shop.nuribooks.books.exception.member;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;

public class AlreadyMemberException extends ResourceAlreadyExistException {
	public AlreadyMemberException(String message) {
		super(message);
	}
}
