package shop.nuribooks.books.member.grade.dto.request;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GradeRegisterRequest(

	@NotBlank(message = "등급명은 반드시 입력해야 합니다.")
	String name,

	@NotNull(message = "포인트 적립률은 반드시 입력해야 합니다.")
	@Min(value = 0, message = "포인트 적립률은 0 이상의 정수입니다.")
	@Max(value = 100, message = "포인트 적립률은 100을 초과할 수 없습니다.")
	Integer pointRate,

	@NotNull(message = "등급의 승급 조건 금액은 반드시 입력해야 합니다.")
	@Column(precision = 11, scale = 2)
	@DecimalMax(value = "100_000_000", message = "등급의 승급 조건 금액은 1억원을 초과할 수 없습니다.")
	BigDecimal requirement
) {}
