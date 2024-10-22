package shop.nuribooks.books.exception.contributor;

public class ContributorNotFoundException extends RuntimeException{
	public ContributorNotFoundException(String message) {
		super(message);
	}

	public ContributorNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
