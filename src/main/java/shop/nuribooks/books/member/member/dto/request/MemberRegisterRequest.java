package shop.nuribooks.books.member.member.dto.request;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.*;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import shop.nuribooks.books.member.member.entity.GenderType;

@Builder
public record MemberRegisterRequest(

	@NotBlank(message = "이름은 반드시 입력해야 합니다.")
	@Size(min = 2, max = 30, message = "이름은 반드시 2자 이상 30자 이하로 입력해야 합니다.")
	String name,

	@NotNull(message = "성별은 반드시 입력해야 합니다.")
	GenderType gender,

	@NotBlank(message = "아이디는 반드시 입력해야 합니다.")
	String username,

	@NotBlank(message = "비밀번호는 반드시 입력해야 합니다.")
	String password,

	@NotBlank(message = "전화번호는 반드시 입력해야 합니다.")
	@Pattern(regexp = "^010\\d{8}$",
		message = "전화번호는 '-' 없이 '010'으로 시작하는 11자리의 숫자로 입력해야 합니다.")
	String phoneNumber,

	@NotBlank(message = "이메일은 반드시 입력해야 합니다.")
	@Pattern(regexp = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$",
		message = "유효한 이메일 형식으로 입력해야 합니다.")
	@Size(max = 30, message = "이메일은 30자 이내로 입력해야 합니다.")
	String email,

	@NotNull(message = "생일은 반드시 입력해야 합니다.")
	@JsonFormat(shape = STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	LocalDate birthday
) {
}

