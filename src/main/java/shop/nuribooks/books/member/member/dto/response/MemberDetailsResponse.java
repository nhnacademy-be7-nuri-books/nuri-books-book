package shop.nuribooks.books.member.member.dto.response;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;

/**
 * ISO 8601 형식에서 날짜와 시간을 표현할 때, 날짜 부분과 시간 부분을 구분하기 위해 'T'를 사용
 */
@Builder
public record MemberDetailsResponse(

	String username,
	String name,
	String phoneNumber,
	String email,
	BigDecimal point,
	BigDecimal totalPaymentAmount,
	String gradeName,
	Integer pointRate,

	@JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
	LocalDateTime createdAt
) {}
