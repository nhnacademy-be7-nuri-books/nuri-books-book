package shop.nuribooks.books.book.point.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import shop.nuribooks.books.book.point.entity.PointPolicy;
import shop.nuribooks.books.book.point.enums.PolicyType;

public record PointPolicyRequest(
	@NotNull(message = "포인트 정책은 필수입니다.")
	PolicyType policyType,
	@NotBlank(message = "이름은 필수입니다.")
	String name,
	@NotNull(message = "정책량은 필수입니다.")
	@Min(value = 0, message = "0 이상의 값이 필요합니다.")
	BigDecimal amount
) {

	public PointPolicy toEntity() {
		return PointPolicy.builder()
			.name(name)
			.amount(amount)
			.policyType(policyType)
			.build();
	}
}
