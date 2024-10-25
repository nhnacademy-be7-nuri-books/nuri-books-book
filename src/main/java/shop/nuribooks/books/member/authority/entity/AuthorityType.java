package shop.nuribooks.books.member.authority.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AuthorityType {

    ADMIN, MEMBER, SELLER;

    @JsonCreator
    public static AuthorityType fromValue(String value) {
        return AuthorityType.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String getValue() {
        return name();
    }
}
