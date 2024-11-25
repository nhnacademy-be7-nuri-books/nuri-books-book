package shop.nuribooks.books.book.coupon.categorycoupon.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.coupon.categorycoupon.service.CategoryCouponService;
import shop.nuribooks.books.book.coupon.service.CouponService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryCouponServiceImpl implements CategoryCouponService {

	private final CouponService couponService;

	@Transactional
	@Override
	public void registerCategoryCoupon() {

	}
}
