package shop.nuribooks.books.exception.member;

public class UserIdNotFoundException extends RuntimeException {
	public UserIdNotFoundException(String message) {
		super(message);
	}
}
