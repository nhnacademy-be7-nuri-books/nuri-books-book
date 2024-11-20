package shop.nuribooks.books.book.coupon.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.coupon.dto.CouponRequest;
import shop.nuribooks.books.book.coupon.dto.CouponResponse;
import shop.nuribooks.books.book.coupon.entity.Coupon;

public interface CouponService {
	Coupon registerCoupon(CouponRequest request);

	Page<CouponResponse> getCoupons(Pageable pageable);

	Coupon getCouponById(Long id);

	void expireCoupon(Long id);

	Coupon updateCoupon(Long id, CouponRequest request);
}
