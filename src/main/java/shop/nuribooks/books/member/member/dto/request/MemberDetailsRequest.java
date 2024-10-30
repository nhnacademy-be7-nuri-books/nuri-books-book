package shop.nuribooks.books.member.member.dto.request;

import lombok.Builder;

@Builder
public record MemberDetailsRequest(

	String userId,
	String password
) {}
