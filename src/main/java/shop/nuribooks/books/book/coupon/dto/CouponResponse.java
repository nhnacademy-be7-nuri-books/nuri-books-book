package shop.nuribooks.books.book.coupon.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.enums.CouponType;
import shop.nuribooks.books.book.coupon.enums.DiscountType;
import shop.nuribooks.books.book.coupon.enums.ExpirationType;

@Builder
public record CouponResponse(
	Long id,
	String name,
	DiscountType discountType,
	int discount,
	BigDecimal minimumOrderPrice,
	BigDecimal maximumDiscountPrice,
	LocalDate createdAt,
	LocalDate expiredAt,
	int period,
	ExpirationType expirationType,
	LocalDateTime expiredDate,
	CouponType couponType
) {
	public static CouponResponse of(Coupon coupon) {
		return CouponResponse.builder()
			.id(coupon.getId())
			.name(coupon.getName())
			.discountType(coupon.getCouponPolicy().getDiscountType())
			.discount(coupon.getCouponPolicy().getDiscount())
			.minimumOrderPrice(coupon.getCouponPolicy().getMinimumOrderPrice())
			.maximumDiscountPrice(coupon.getCouponPolicy().getMaximumDiscountPrice())
			.createdAt(coupon.getCreatedAt())
			.expiredAt(coupon.getExpiredAt())
			.expirationType(coupon.getExpirationType())
			.period(coupon.getPeriod())
			.expiredDate(coupon.getDeletedAt())
			.couponType(coupon.getCouponType())
			.build();
	}
}
