package shop.nuribooks.books.member.state.dto.response;

import shop.nuribooks.books.member.state.entity.MemberState;
import shop.nuribooks.books.member.state.entity.MemberStateType;

public record MemberStateResponse(MemberStateType memberStateType){

    public static MemberStateResponse of(MemberState memberState) {
        return new MemberStateResponse(memberState.getMemberStateType());
    }
}
