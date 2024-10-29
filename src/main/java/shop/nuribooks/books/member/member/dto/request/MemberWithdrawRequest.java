package shop.nuribooks.books.member.member.dto.request;

import lombok.Builder;

@Builder
public record MemberWithdrawRequest (

	String userId,
	String password
) {}
