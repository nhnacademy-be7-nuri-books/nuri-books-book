package shop.nuribooks.books.book.coupon.dto;

import java.math.BigDecimal;

import shop.nuribooks.books.book.coupon.enums.CouponType;

public record CouponAppliedOrderDto(
	String name,
	BigDecimal discountPrice,
	CouponType couponType
) {
}
