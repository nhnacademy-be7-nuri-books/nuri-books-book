package shop.nuribooks.books.exception.contributor;

public class DuplicateEntityException extends RuntimeException{
	public DuplicateEntityException(String message) {
		super(message);
	}

	public DuplicateEntityException(String message, Throwable cause) {
		super(message, cause);
	}


}
