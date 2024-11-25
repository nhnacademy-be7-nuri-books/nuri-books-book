package shop.nuribooks.books.book.coupon.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.coupon.dto.MemberCouponRegisterRequest;
import shop.nuribooks.books.book.coupon.dto.MemberCouponResponse;

public interface MemberCouponService {

	void registerMemberCoupon(MemberCouponRegisterRequest memberCouponRegisterRequest);

	List<MemberCouponResponse> getAllCouponByMemberId(Long memberId);

	void updateIsUsed(Long memberCouponId);

	void deleteMemberCoupon(Long memberCouponId);

	Page<MemberCouponResponse> getAvailableCouponsByMemberId(Long memberId, Pageable pageable);

	Page<MemberCouponResponse> getExpiredOrUsedCouponsByMemberId(Long memberId, Pageable pageable);
}
