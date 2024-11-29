package shop.nuribooks.books.book.coupon.dto;

import java.math.BigDecimal;

import lombok.Builder;
import shop.nuribooks.books.book.coupon.enums.CouponType;

@Builder
public record CouponAppliedOrderDto(
	String name,
	BigDecimal discountPrice,
	CouponType couponType
) {
}
