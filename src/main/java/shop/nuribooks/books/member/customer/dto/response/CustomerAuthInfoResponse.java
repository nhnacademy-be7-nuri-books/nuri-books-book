package shop.nuribooks.books.member.customer.dto.response;

import lombok.Builder;

@Builder
public record CustomerAuthInfoResponse(

	Long customerId,
	String password,
	String email
) {
}
