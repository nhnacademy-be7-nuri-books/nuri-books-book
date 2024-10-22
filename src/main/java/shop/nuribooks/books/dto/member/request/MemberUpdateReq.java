package shop.nuribooks.books.dto.member.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class MemberUpdateReq {

	@NotBlank(message = "이름은 반드시 입력해야 합니다.")
	private String name;

	@NotBlank(message = "비밀번호는 반드시 입력해야 합니다.")
	@Size(min = 8, message = "비밀번호는 반드시 8자 이상으로 입력해야 합니다.")
	private String password;

	@NotBlank(message = "전화번호는 반드시 입력해야 합니다.")
	private String phoneNumber;
}
