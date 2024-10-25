package shop.nuribooks.books.member.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CustomerUpdateRequest {

	@NotBlank(message = "이름은 반드시 입력해야 합니다.")
	private String name;

	@NotBlank(message = "전화번호 반드시 입력해야 합니다.")
	private String phoneNumber;
}
