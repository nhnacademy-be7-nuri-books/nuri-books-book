package shop.nuribooks.books.book.point.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import shop.nuribooks.books.book.point.entity.PointPolicy;
import shop.nuribooks.books.book.point.enums.PolicyType;

public record PointPolicyRequest(
	@NotBlank
	PolicyType policyType,
	@NotBlank
	String name,
	@Min(0)
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
