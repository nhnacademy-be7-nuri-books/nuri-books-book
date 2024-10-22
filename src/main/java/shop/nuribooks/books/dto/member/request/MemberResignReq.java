package shop.nuribooks.books.dto.member.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberResignReq {

	@NotBlank(message = "아이디는 반드시 입력해야 합니다.")
	@Size(min = 8, max = 20, message = "아이디는 반드시 8자 이상 20자 이하로 입력해야 합니다.")
	private String userId;

	@NotBlank(message = "비밀번호는 반드시 입력해야 합니다.")
	@Size(min = 8, message = "비밀번호는 반드시 8자 이상으로 입력해야 합니다.")
	private String password;
}
