package shop.nuribooks.books.exception.member;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class MemberNotFoundException extends ResourceNotFoundException {
	public MemberNotFoundException(String message) {
		super(message);
	}
}
