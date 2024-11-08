package shop.nuribooks.books.member.member.entity;

import java.util.Objects;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusType {

	ACTIVE, INACTIVE, WITHDRAWN;

	@JsonValue
	public String getValue() {
		return name();
	}

	@JsonCreator
	public static StatusType fromValue(String value) {

		if (Objects.isNull(value)) {
			return null;
		}

		return Stream.of(StatusType.values())
			.filter(statusType -> statusType.getValue().equals(value.toUpperCase()))
			.findFirst()
			.orElse(null);
	}
}
