package shop.nuribooks.books.book.coupon.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import shop.nuribooks.books.book.coupon.enums.CouponType;
import shop.nuribooks.books.book.coupon.enums.DiscountType;
import shop.nuribooks.books.book.coupon.enums.ExpirationType;
import shop.nuribooks.books.book.coupon.enums.IssuanceType;

@Builder
public record CouponResponse(
	Long id,
	String name,
	CouponType couponType,
	DiscountType discountType,
	Integer discount,
	BigDecimal minimumOrderPrice,
	BigDecimal maximumDiscountPrice,
	ExpirationType expirationType,
	LocalDate expiredAt,
	Integer period,
	IssuanceType issuanceType,
	Integer quantity,
	Long itemId,
	String itemName,
	LocalDate createdAt,
	LocalDateTime deletedAt
) {
}
