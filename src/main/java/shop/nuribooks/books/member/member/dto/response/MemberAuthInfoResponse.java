package shop.nuribooks.books.member.member.dto.response;

import lombok.Builder;

@Builder
public record MemberAuthInfoResponse(

	String username, // -> userId
	String password,
	String role // "ROLE_ADMIN", "ROLE_MEMBER", "ROLE_SELLER"
) {}