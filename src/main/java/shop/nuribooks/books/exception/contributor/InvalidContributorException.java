package shop.nuribooks.books.exception.contributor;

public class InvalidContributorException extends IllegalArgumentException {
	public InvalidContributorException(String message) {
		super(message);
	}

	public InvalidContributorException(String message, Throwable cause) {
		super(message, cause);
	}
}
