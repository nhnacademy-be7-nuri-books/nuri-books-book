package shop.nuribooks.books.member.member.dto.response;

import lombok.Builder;

@Builder
public record MemberUpdateResponse (

	String name,
	String phoneNumber
) {}
