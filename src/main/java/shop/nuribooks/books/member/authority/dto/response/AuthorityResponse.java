package shop.nuribooks.books.member.authority.dto.response;

import shop.nuribooks.books.member.authority.entity.Authority;
import shop.nuribooks.books.member.authority.entity.AuthorityType;

public record AuthorityResponse(AuthorityType authorityType) {

    public static AuthorityResponse of(Authority authority) {
        return new AuthorityResponse(authority.getAuthorityType());
    }
}
