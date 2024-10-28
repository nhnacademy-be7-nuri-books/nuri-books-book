package shop.nuribooks.books.member.authority.dto.requset;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
