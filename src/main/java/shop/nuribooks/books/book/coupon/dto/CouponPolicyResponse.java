package shop.nuribooks.books.book.coupon.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import shop.nuribooks.books.book.point.enums.PolicyType;

public record CouponPolicyResponse(
	Long id,
	PolicyType policyType,
	String name,
	int discount,
	BigDecimal minimumOrderPrice,
	BigDecimal maximumDiscountPrice,
	LocalDateTime startedAt,
	LocalDateTime endedAt) {

}
