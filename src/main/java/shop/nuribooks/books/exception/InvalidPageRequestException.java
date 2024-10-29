package shop.nuribooks.books.exception;

public class InvalidPageRequestException extends BadRequestException {
	public InvalidPageRequestException(String message) {
		super(message);
	}
}
