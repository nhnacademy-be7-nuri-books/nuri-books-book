package shop.nuribooks.books.book.coupon.repository.querydsl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.coupon.dto.MemberCouponResponse;

public interface MemberCouponRepositoryCustom {
	List<MemberCouponResponse> findAllByMemberId(Long memberId);

	Page<MemberCouponResponse> findAvailableCouponsByMemberId(Long memberId, Pageable pageable);

	Page<MemberCouponResponse> findExpiredOrUsedCouponsByMemberId(Long memberId, Pageable pageable);
}
