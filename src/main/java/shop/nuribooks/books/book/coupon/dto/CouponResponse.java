package shop.nuribooks.books.book.coupon.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.enums.ExpirationType;
import shop.nuribooks.books.book.point.enums.PolicyType;

@Builder
public record CouponResponse(
	Long id,
	PolicyType policyType,
	String name,
	int discount,
	BigDecimal minimumOrderPrice,
	BigDecimal maximumDiscountPrice,
	LocalDate createdAt,
	LocalDate expiredAt,
	int period,
	ExpirationType expirationType
) {
	public static CouponResponse of(Coupon coupon) {
		return CouponResponse.builder()
			.id(coupon.getId())
			.name(coupon.getName())
			.policyType(coupon.getPolicyType())
			.discount(coupon.getDiscount())
			.minimumOrderPrice(coupon.getMinimumOrderPrice())
			.maximumDiscountPrice(coupon.getMaximumDiscountPrice())
			.createdAt(coupon.getCreatedAt())
			.expiredAt(coupon.getExpiredAt())
			.expirationType(coupon.getExpirationType())
			.period(coupon.getPeriod())
			.build();
	}
}
