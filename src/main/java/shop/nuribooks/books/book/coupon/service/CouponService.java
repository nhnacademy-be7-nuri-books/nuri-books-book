package shop.nuribooks.books.book.coupon.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.coupon.dto.CouponRequest;
import shop.nuribooks.books.book.coupon.dto.CouponResponse;
import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.enums.CouponType;
import shop.nuribooks.books.member.member.entity.Member;

public interface CouponService {
	Coupon registerCoupon(CouponRequest request);

	Page<CouponResponse> getCoupons(CouponType type, Pageable pageable);

	Page<CouponResponse> getAllCoupons(Pageable pageable);

	CouponResponse getCouponById(Long id);

	void expireCoupon(Long id);

	Coupon updateCoupon(Long id, CouponRequest request);

	void issueWelcomeCoupon(Member member);

	// boolean isCouponApplicableToOrder(Coupon coupon, List<BookOrderResponse> bookOrderResponses);
}
