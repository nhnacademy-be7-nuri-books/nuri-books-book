package shop.nuribooks.books.exception.member;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class MemberCartNotFoundException extends ResourceNotFoundException {
	public MemberCartNotFoundException(String message) {
		super(message);
	}
}
