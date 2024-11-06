package shop.nuribooks.books.book.contributor.entity;

import shop.nuribooks.books.exception.contributor.InvalidContributorRoleException;

public enum ContributorRoleEnum {
	AUTHOR("지은이"),
	TRANSLATOR("옮긴이"),
	ORIGINAL_AUTHOR("원작"),
	EDITOR("엮은이"),
	COAUTHOR("공저자"),
	SUPERVISOR("감수자"),
	COMPILER("편집자"),
	TRANSCRIBER("필사자"),
	TEXT("텍스트"),
	ILLUSTRATION("삽화가");

	private final String korName;

	ContributorRoleEnum(String korName) {
		this.korName = korName;
	}

	public String getKorName() {
		return korName;
	}

	public static ContributorRoleEnum fromString(String name) {
		try {
			return ContributorRoleEnum.valueOf(name.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new InvalidContributorRoleException(name);
		}
	}

	public static ContributorRoleEnum fromStringKor(String name) {
		for(ContributorRoleEnum state : ContributorRoleEnum.values()) {
			if(state.name().equalsIgnoreCase(name) || state.getKorName().equals(name)) {
				return state;
			}
		}
		throw new InvalidContributorRoleException(name);
	}
}
