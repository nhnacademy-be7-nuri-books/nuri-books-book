package shop.nuribooks.books.member.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class CustomerUpdateResponse {

	private String name;
	private String phoneNumber;
}
