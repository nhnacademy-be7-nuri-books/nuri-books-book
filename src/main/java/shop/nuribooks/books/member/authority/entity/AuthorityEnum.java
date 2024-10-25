package shop.nuribooks.books.member.authority.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AuthorityEnum {

	ADMIN, MEMBER, SELLER;

	@JsonValue
	public String getValue() {
		return name();
	}
}
