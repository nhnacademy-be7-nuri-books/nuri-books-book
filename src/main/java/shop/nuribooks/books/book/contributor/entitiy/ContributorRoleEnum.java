package shop.nuribooks.books.book.contributor.entitiy;

import shop.nuribooks.books.exception.contributor.InvalidContributorRoleException;

public enum ContributorRoleEnum {
	AUTHOR, COAUTHOR, SUPERVISOR, COMPILER, TRANSLATOR, TRANSCRIBER, TEXT, ILLUSTRATION;

	public static ContributorRoleEnum fromString(String name) {
		try {
			return ContributorRoleEnum.valueOf(name.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new InvalidContributorRoleException(name);
		}
	}
}
