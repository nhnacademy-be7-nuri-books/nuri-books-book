package shop.nuribooks.books.member.member.entity;

import java.util.Objects;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AuthorityType {

	ADMIN, MEMBER, SELLER;

	@JsonCreator
	public static AuthorityType fromValue(String value) {

		if (Objects.isNull(value)) {
			return null;
		}

		return Stream.of(AuthorityType.values())
			.filter(authorityType -> authorityType.getValue().equals(value.toUpperCase()))
			.findFirst()
			.orElse(null);
	}

	@JsonValue
	public String getValue() {
		return name();
	}
}

