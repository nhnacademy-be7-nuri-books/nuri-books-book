package shop.nuribooks.books.member.member.dto.response;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.*;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import shop.nuribooks.books.member.member.entity.GenderType;

@Builder
public record MemberRegisterResponse (

	String name,
	GenderType gender,
	String userId,
	String phoneNumber,
	String email,

	@JsonFormat(shape = STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	LocalDate birthday
) {}
