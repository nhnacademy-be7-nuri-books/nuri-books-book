package shop.nuribooks.books.member.member.dto.response;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.member.entity.AuthorityType;
import shop.nuribooks.books.member.member.entity.GenderType;
import shop.nuribooks.books.member.member.entity.StatusType;

/**
 * ISO 8601 형식에서 날짜와 시간을 표현할 때, 날짜 부분과 시간 부분을 구분하기 위해 'T'를 사용
 */
@Builder
public record MemberDetailsResponse(

	String name,
	GenderType gender,
	String phoneNumber,
	String email,

	@JsonFormat(shape = STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	LocalDate birthday,

	String username,
	String password,
	BigDecimal point,
	BigDecimal totalPaymentAmount,
	AuthorityType authority,
	Grade grade,
	StatusType status,

	@JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
	LocalDateTime createdAt,

	@JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
	LocalDateTime latestLoginAt
) {}
