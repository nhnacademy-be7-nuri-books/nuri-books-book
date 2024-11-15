package shop.nuribooks.books.book.coupon.repository.querydsl;

import java.util.List;

import shop.nuribooks.books.book.coupon.dto.MemberCouponResponse;

public interface MemberCouponRepositoryCustom {
	List<MemberCouponResponse> findAllByMemberId(Long memberId);
}
