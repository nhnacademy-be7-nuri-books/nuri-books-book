package shop.nuribooks.books.book.coupon.categorycoupon.service;

import shop.nuribooks.books.book.coupon.categorycoupon.dto.CategoryCouponRequest;
import shop.nuribooks.books.book.coupon.categorycoupon.dto.CategoryCouponResponse;

public interface CategoryCouponService {
	void registerCategoryCoupon(CategoryCouponRequest categoryCouponRequest);

	CategoryCouponResponse getCategoryCoupon(Long categoryId);

}
