package shop.nuribooks.books.member.customer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class CustomerRegisterResponse {

	private String name;
	private String phoneNumber;
	private String email;
}
