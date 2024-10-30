package shop.nuribooks.books.member.member.entity;

import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AuthorityType {

	ADMIN, MEMBER, SELLER;

	@JsonValue
	public String getValue() {
		return name();
	}

	@JsonCreator
	public static AuthorityType fromValue(String value) {
		return Stream.of(AuthorityType.values())
			.filter(authorityType -> authorityType.getValue().equals(value.toUpperCase()))
			.findFirst()
			.orElse(null);
	}
}
