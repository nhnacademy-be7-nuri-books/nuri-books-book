package shop.nuribooks.books.exception.member;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class UsernameNotFoundException extends ResourceNotFoundException {
	public UsernameNotFoundException(String message) {
		super(message);
	}
}
