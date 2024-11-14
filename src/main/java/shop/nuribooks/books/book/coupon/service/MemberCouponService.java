package shop.nuribooks.books.book.coupon.service;

import java.util.List;

import shop.nuribooks.books.book.coupon.dto.MemberCouponResponse;

public interface MemberCouponService {

	void registerMemberCoupon(Long memberId, Long couponId);

	List<MemberCouponResponse> getAllCouponByMemberId(Long memberId);

	void updateIsUsed(Long memberCouponId);

	void deleteMemberCoupon(Long memberCouponId);
}
