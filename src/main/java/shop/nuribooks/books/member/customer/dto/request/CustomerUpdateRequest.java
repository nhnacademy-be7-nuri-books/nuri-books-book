package shop.nuribooks.books.member.customer.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CustomerUpdateRequest (

	@NotBlank(message = "이름은 반드시 입력해야 합니다.")
	String name,

	@NotBlank(message = "전화번호 반드시 입력해야 합니다.")
	String phoneNumber
) {}
