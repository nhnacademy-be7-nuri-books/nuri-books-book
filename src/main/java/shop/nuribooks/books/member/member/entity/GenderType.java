package shop.nuribooks.books.member.member.entity;

import java.util.Objects;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum GenderType {

	MALE, FEMALE, OTHER;

	@JsonValue
	public String getValue() {
		return name();
	}

	@JsonCreator
	public static GenderType fromValue(String value) {

		if (Objects.isNull(value)) {
			return null;
		}

		return Stream.of(GenderType.values())
			.filter(genderType -> genderType.getValue().equals(value.toUpperCase()))
			.findFirst()
			.orElse(null);
	}
}
