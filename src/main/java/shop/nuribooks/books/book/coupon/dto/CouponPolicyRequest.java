package shop.nuribooks.books.book.coupon.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import shop.nuribooks.books.book.coupon.entity.CouponPolicy;
import shop.nuribooks.books.book.point.enums.PolicyType;

public record CouponPolicyRequest(
	@NotNull(message = "쿠폰 정책은 필수입니다.")
	PolicyType policyType,

	@NotNull(message = "이름은 필수입니다.")
	@Size(min = 2, max = 20)
	String name,

	@NotNull(message = "할인율은 필수입니다.")
	@PositiveOrZero
	int discount,

	@NotNull(message = "최소 주문 금액은 필수입니다.")
	@Digits(integer = 10, fraction = 0)
	BigDecimal minimumOrderPrice,

	@NotNull(message = "최대 할인 금액은 필수입니다.")
	@Digits(integer = 10, fraction = 0)
	BigDecimal maximumDiscountPrice,

	@NotNull(message = "시작일시는 필수입니다.")
	@FutureOrPresent
	LocalDateTime startedAt,

	LocalDateTime endedAt) {

	public CouponPolicy toEntity() {
		return CouponPolicy.builder()
			.policyType(policyType)
			.name(name)
			.discount(discount)
			.minimumOrderPrice(minimumOrderPrice)
			.maximumDiscountPrice(maximumDiscountPrice)
			.startedAt(startedAt)
			.endedAt(endedAt)
			.build();
	}
}
