package shop.nuribooks.books.member.authority.dto.response;

import jakarta.validation.constraints.NotBlank;
import shop.nuribooks.books.member.authority.entity.Authority;
import shop.nuribooks.books.member.authority.entity.AuthorityEnum;

public record AuthorityResponse(AuthorityEnum authorityEnum) {

    public static AuthorityResponse of(Authority authority) {
        return new AuthorityResponse(authority.getName());
    }
}
