package shop.nuribooks.books.member.state.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.stream.Stream;

public enum MemberStateType {

    ACTIVE,
    DORMANT,
    WITHDRAW;

    @JsonValue
    public String getValue() {
        return name();
    }

    @JsonCreator
    public static MemberStateType fromValue(String value) {
        return Stream.of(MemberStateType.values())
                .filter(memberStateType -> memberStateType.getValue().equals(value.toUpperCase()))
                .findFirst()
                .orElse(null);
    }
}
