package shop.nuribooks.books.member.member.dto.response;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import shop.nuribooks.books.member.member.entity.AuthorityType;
import shop.nuribooks.books.member.member.entity.GenderType;
import shop.nuribooks.books.member.member.entity.StatusType;

@Builder
public record MemberDetailsResponse(

	AuthorityType authority,
	String grade,
	StatusType status,
	GenderType gender,
	String name,
	String phoneNumber,
	String email,
	String userId,
	String password,

	@JsonFormat(shape = STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	LocalDate birthday,

	/**
	 * ISO 8601 형식에서 날짜와 시간을 표현할 때, 날짜 부분과 시간 부분을 구분하기 위해 'T'를 사용
	 */
	@JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
	LocalDateTime createdAt,

	@JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
	LocalDateTime latestLoginAt,

	BigDecimal point,
	BigDecimal totalPaymentAmount
) {}
