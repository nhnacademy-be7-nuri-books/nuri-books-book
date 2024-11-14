package shop.nuribooks.books.book.coupon.service;

import shop.nuribooks.books.book.coupon.dto.CouponRequest;
import shop.nuribooks.books.book.coupon.entity.Coupon;

public interface CouponService {
	Coupon registerCoupon(CouponRequest request);
}
