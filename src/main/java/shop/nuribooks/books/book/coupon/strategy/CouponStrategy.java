package shop.nuribooks.books.book.coupon.strategy;

import shop.nuribooks.books.book.coupon.dto.CouponRequest;
import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.entity.CouponPolicy;

public interface CouponStrategy {
	Coupon registerCoupon(CouponRequest request, CouponPolicy couponPolicy);
}

