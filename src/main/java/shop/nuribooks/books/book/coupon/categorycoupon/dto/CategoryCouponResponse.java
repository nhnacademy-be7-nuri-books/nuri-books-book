package shop.nuribooks.books.book.coupon.categorycoupon.dto;

import shop.nuribooks.books.book.coupon.categorycoupon.entity.CategoryCoupon;

public record CategoryCouponResponse(Long couponId) {
	public static CategoryCoupon of(CategoryCoupon categoryCoupon) {
		return CategoryCoupon.builder()
			.coupon(categoryCoupon.getCoupon())
			.category(categoryCoupon.getCategory())
			.build();
	}

	public CategoryCouponResponse(CategoryCoupon categoryCoupon) {
		this(categoryCoupon.getCoupon().getId());
	}
}
