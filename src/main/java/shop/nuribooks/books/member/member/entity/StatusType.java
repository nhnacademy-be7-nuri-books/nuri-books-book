package shop.nuribooks.books.member.member.entity;

import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusType {

	ACTIVE, DORMANT, WITHDRAWN;

	@JsonValue
	public String getValue() {
		return name();
	}

	@JsonCreator
	public static StatusType fromValue(String value) {
		return Stream.of(StatusType.values())
			.filter(memberStateType -> memberStateType.getValue().equals(value.toUpperCase()))
			.findFirst()
			.orElse(null);
	}
}
