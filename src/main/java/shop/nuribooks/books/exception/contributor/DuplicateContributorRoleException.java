package shop.nuribooks.books.exception.contributor;

public class DuplicateContributorRoleException extends RuntimeException{
	public DuplicateContributorRoleException(String message) {
		super(message);
	}

	public DuplicateContributorRoleException(String message, Throwable cause) {
		super(message, cause);
	}


}
