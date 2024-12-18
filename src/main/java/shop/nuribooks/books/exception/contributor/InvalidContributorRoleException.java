package shop.nuribooks.books.exception.contributor;

import shop.nuribooks.books.exception.BadRequestException;

public class InvalidContributorRoleException extends BadRequestException {

	public InvalidContributorRoleException(String message) {
		super(message);
	}

	public InvalidContributorRoleException() {
		super("잘못된 형식입니다.");
	}

}
