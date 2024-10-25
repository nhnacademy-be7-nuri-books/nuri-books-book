package shop.nuribooks.books.member.authority.dto.requset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import shop.nuribooks.books.member.authority.entity.AuthorityEnum;

public record AuthorityEditRequest(@NotNull Long authorityId, @NotBlank AuthorityEnum authorityEnum) {
}
