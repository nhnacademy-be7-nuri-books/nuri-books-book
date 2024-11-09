package shop.nuribooks.books.book.contributor.entity;

import shop.nuribooks.books.exception.contributor.InvalidContributorRoleException;

public enum ContributorRoleEnum {
	AUTHOR("지은이"),
	EDITOR("엮은이"),
	TRANSLATOR("옮긴이"),
	ORIGINAL_AUTHOR("원작"),
	PHOTOS("사진"),
	ILLUSTRATOR("그림"),
	CONSULTANT("감수");

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
