package shop.nuribooks.books.exception.member;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class UserIdNotFoundException extends ResourceNotFoundException {
	public UserIdNotFoundException(String message) {
		super(message);
	}
}
