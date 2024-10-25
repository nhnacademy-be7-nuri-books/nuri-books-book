package shop.nuribooks.books.member.customer.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CustomerRegisterRequest {

	@NotBlank(message = "이름은 반드시 입력해야 합니다.")
	private String name;

	@NotBlank(message = "비밀번호는 반드시 입력해야 합니다.")
	private String password;

	@NotBlank(message = "전화번호는 반드시 입력해야 합니다.")
	private String phoneNumber;

	@NotBlank(message = "이메일은 반드시 입력해야 합니다.")
	@Email(message = "유효한 이메일 형식으로 입력해야 합니다.")
	private String email;
}
