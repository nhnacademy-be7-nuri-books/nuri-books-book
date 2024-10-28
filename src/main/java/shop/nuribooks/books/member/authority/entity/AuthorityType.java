package shop.nuribooks.books.member.authority.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.stream.Stream;

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
