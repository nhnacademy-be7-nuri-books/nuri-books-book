package shop.nuribooks.books.book.coupon.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import shop.nuribooks.books.book.coupon.entity.CouponPolicy;
import shop.nuribooks.books.book.point.enums.PolicyType;

@Builder
public record CouponPolicyResponse(
	Long id,
	PolicyType policyType,
	String name,
	int discount,
	BigDecimal minimumOrderPrice,
	BigDecimal maximumDiscountPrice,
	LocalDateTime startedAt,
	LocalDateTime endedAt) {

	public static CouponPolicyResponse of(CouponPolicy couponPolicy) {
		return CouponPolicyResponse.builder()
			.id(couponPolicy.getId())
			.policyType(couponPolicy.getPolicyType())
			.name(couponPolicy.getName())
			.discount(couponPolicy.getDiscount())
			.minimumOrderPrice(couponPolicy.getMinimumOrderPrice())
			.maximumDiscountPrice(couponPolicy.getMaximumDiscountPrice())
			.startedAt(couponPolicy.getStartedAt())
			.endedAt(couponPolicy.getEndedAt())
			.build();
	}

}
