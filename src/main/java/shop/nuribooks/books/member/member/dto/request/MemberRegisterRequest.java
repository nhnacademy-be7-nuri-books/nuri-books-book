package shop.nuribooks.books.member.member.dto.request;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.*;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record MemberRegisterRequest (

	@NotBlank(message = "이름은 반드시 입력해야 합니다.")
	String name,

	@NotBlank(message = "아이디는 반드시 입력해야 합니다.")
	@Size(min = 8, max = 20, message = "아이디는 반드시 8자 이상 20자 이하로 입력해야 합니다.")
	String userId,

	@NotBlank(message = "비밀번호는 반드시 입력해야 합니다.")
	String password,

	@NotBlank(message = "전화번호는 반드시 입력해야 합니다.")
	String phoneNumber,

	@NotBlank(message = "이메일은 반드시 입력해야 합니다.")
	@Email(message = "유효한 이메일 형식으로 입력해야 합니다.")
	String email,

	@NotNull(message = "생일은 반드시 입력해야 합니다.")
	@JsonFormat(shape = STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	LocalDate birthday
) {}

