package shop.nuribooks.books.exception.contributor;

public class ContributorRoleNotFoundException extends RuntimeException{
	public ContributorRoleNotFoundException(String message) {
		super(message);
	}

	public ContributorRoleNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
