package shop.nuribooks.books.exception.member;

public class UserIdAlreadyExistsException extends RuntimeException {
	public UserIdAlreadyExistsException(String message) {
		super(message);
	}
}
