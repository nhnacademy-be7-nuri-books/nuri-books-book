package shop.nuribooks.books.member.state.dto.request;

import jakarta.validation.constraints.NotNull;
import shop.nuribooks.books.member.state.entity.MemberStateType;

public record MemberStateEditRequest (@NotNull MemberStateType memberStateType){
}
