package shop.nuribooks.books.exception.contributor;

public class InvalidContributorRoleException extends IllegalArgumentException {

	public InvalidContributorRoleException(String message) {
		super(message);
	}

	public InvalidContributorRoleException(String message, Throwable cause) {
		super(message, cause);
	}
}
