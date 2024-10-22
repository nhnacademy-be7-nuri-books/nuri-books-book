package shop.nuribooks.books.dto.member.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class MemberCreateReq {

	@NotBlank(message = "이름은 반드시 입력해야 합니다.")
	private String name;

	@NotBlank(message = "아이디는 8자 이상 20자 이하로 반드시 입력해야 합니다.")
	@Size(min = 8, max = 20)
	private String userId;

	@NotBlank(message = "비밀번호는 반드시 입력해야 합니다.")
	private String password;

	@NotBlank(message = "전화번호는 반드시 입력해야 합니다.")
	private String phoneNumber;

	@NotBlank(message = "이메일은 반드시 입력해야 합니다.")
	private String email;

	@NotNull(message = "생일은 반드시 입력해야 합니다.")
	private LocalDate birthday;

}

