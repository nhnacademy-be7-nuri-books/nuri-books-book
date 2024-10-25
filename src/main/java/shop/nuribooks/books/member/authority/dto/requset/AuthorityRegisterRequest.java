package shop.nuribooks.books.member.authority.dto.requset;

import jakarta.validation.constraints.NotNull;
import shop.nuribooks.books.member.authority.entity.Authority;
import shop.nuribooks.books.member.authority.entity.AuthorityType;

public record AuthorityRegisterRequest(@NotNull AuthorityType authorityType) {

    public Authority toEntity() {
        return Authority.builder()
                .authorityType(authorityType)
                .build();
    }
}
