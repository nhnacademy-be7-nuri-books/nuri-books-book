package shop.nuribooks.books.book.coupon.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.coupon.dto.CouponPolicyRequest;
import shop.nuribooks.books.book.coupon.dto.CouponPolicyResponse;
import shop.nuribooks.books.book.coupon.entity.CouponPolicy;

public interface CouponPolicyService {

	Page<CouponPolicyResponse> getCouponPolicies(Pageable pageable);

	CouponPolicy registerCouponPolicy(CouponPolicyRequest request);

	CouponPolicy updateCouponPolicy(Long id, CouponPolicyRequest request);

	void deleteCouponPolicy(Long id);
}
