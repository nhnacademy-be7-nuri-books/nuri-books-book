package shop.nuribooks.books.member.member.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AuthorityEnum {

	ADMIN, MEMBER, SELLER;

	@JsonValue
	public String getValue() {
		return name();
	}
}
