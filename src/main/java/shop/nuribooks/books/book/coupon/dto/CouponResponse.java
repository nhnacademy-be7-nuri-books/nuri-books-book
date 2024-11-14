package shop.nuribooks.books.book.coupon.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import shop.nuribooks.books.book.coupon.enums.ExpirationType;

public record CouponResponse(
	Long id,
	String name,
	Long discount,
	BigDecimal minimumOrderPrice,
	BigDecimal maximumDiscountPrice,
	LocalDate createdAt,
	LocalDate expiredAt,
	int period,
	ExpirationType expirationType
) {
}
