package shop.nuribooks.books.member.grade.dto.response;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record GradeRegisterResponse(

	String name,
	Integer pointRate,
	BigDecimal requirement
) {}