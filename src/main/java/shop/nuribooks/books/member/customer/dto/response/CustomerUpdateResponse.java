package shop.nuribooks.books.member.customer.dto.response;

import lombok.Builder;

@Builder
public record CustomerUpdateResponse (

	String name,
	String phoneNumber
) {}
