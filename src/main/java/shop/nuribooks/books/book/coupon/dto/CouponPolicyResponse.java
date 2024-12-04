package shop.nuribooks.books.book.coupon.dto;

import java.math.BigDecimal;

import lombok.Builder;
import shop.nuribooks.books.book.coupon.entity.CouponPolicy;
import shop.nuribooks.books.book.coupon.enums.DiscountType;

@Builder
public record CouponPolicyResponse(
	Long id,
	String name,
	DiscountType discountType,
	BigDecimal minimumOrderPrice,
	BigDecimal maximumDiscountPrice,
	int discount) {

	public static CouponPolicyResponse of(CouponPolicy couponPolicy) {
		return CouponPolicyResponse.builder()
			.id(couponPolicy.getId())
			.name(couponPolicy.getName())
			.discountType(couponPolicy.getDiscountType())
			.minimumOrderPrice(couponPolicy.getMinimumOrderPrice())
			.maximumDiscountPrice(couponPolicy.getMaximumDiscountPrice())
			.discount(couponPolicy.getDiscount())
			.build();
	}
}
