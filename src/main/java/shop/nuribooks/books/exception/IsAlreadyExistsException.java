package shop.nuribooks.books.exception;

public class IsAlreadyExistsException extends RuntimeException {
	public IsAlreadyExistsException(String message) {
		super(message);
	}
}
