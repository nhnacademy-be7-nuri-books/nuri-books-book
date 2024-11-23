package shop.nuribooks.books.book.coupon.bookcoupon.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record BookCouponRequest(
	Long couponId,
	List<Long> bookIds
) {
}
