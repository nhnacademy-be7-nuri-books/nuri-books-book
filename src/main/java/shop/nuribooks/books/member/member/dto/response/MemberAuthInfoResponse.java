package shop.nuribooks.books.member.member.dto.response;

import lombok.Builder;

@Builder
public record MemberAuthInfoResponse(

	Long customerId,
	String username,
	String password,
	String role // "ROLE_ADMIN", "ROLE_MEMBER", "ROLE_SELLER"
) {}
