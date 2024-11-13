package shop.nuribooks.books.book.coupon.service;

import shop.nuribooks.books.book.coupon.dto.CouponPolicyRequest;
import shop.nuribooks.books.book.coupon.entity.CouponPolicy;

public interface CouponPolicyService {

	CouponPolicy registerCouponPolicy(CouponPolicyRequest request);
}
