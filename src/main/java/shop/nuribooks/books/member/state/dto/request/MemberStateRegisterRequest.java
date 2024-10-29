package shop.nuribooks.books.member.state.dto.request;

import jakarta.validation.constraints.NotNull;
import shop.nuribooks.books.member.state.entity.MemberState;
import shop.nuribooks.books.member.state.entity.MemberStateType;

public record MemberStateRegisterRequest(@NotNull MemberStateType memberStateType) {

    public MemberState toEntity() {
        return MemberState.builder()
                .memberStateType(memberStateType)
                .build();
    }
}
