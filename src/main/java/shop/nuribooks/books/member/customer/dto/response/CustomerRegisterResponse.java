package shop.nuribooks.books.member.customer.dto.response;

import lombok.Builder;

@Builder
public record CustomerRegisterResponse(

	String name,
	String phoneNumber,
	String email
) {
}

