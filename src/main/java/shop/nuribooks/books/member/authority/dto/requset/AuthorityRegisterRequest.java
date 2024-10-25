package shop.nuribooks.books.member.authority.dto.requset;

import jakarta.validation.constraints.NotBlank;
import shop.nuribooks.books.member.authority.entity.Authority;
import shop.nuribooks.books.member.authority.entity.AuthorityEnum;

public record AuthorityRegisterRequest(@NotBlank AuthorityEnum authorityEnum) {

    public Authority toEntity() {
        return Authority.builder()
                .name(authorityEnum)
                .build();
    }
}
