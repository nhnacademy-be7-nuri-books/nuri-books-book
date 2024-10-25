package shop.nuribooks.books.member.authority.dto.requset;


import jakarta.validation.constraints.NotNull;
import shop.nuribooks.books.member.authority.entity.AuthorityType;

public record AuthorityEditRequest(@NotNull AuthorityType authorityType) {
}
