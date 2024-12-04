package shop.nuribooks.books.book.coupon.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import shop.nuribooks.books.book.coupon.enums.DiscountType;

@Builder
public record MemberCouponOrderDto(
	@NotNull long couponId,
	@NotNull long memberCouponId,
	@NotNull String couponName,
	@NotNull DiscountType discountType,
	@NotNull int discount,
	@NotNull BigDecimal minimumOrderPrice,
	@NotNull BigDecimal maximumDiscountPrice,
	@NotNull boolean isUsed,
	@NotNull LocalDate createdAt,
	@NotNull LocalDate expiredAt) {
}
