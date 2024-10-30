package shop.nuribooks.books.member.member.dto.response;

import lombok.Builder;

@Builder
public record MemberCheckResponse (

	String name,
	String password,
	String authority // "ROLE_ADMIN", "ROLE_MEMBER", "ROLE_SELLER"
) {}