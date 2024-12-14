package shop.nuribooks.books.book.coupon.repository.querydsl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.coupon.dto.MemberCouponOrderDto;
import shop.nuribooks.books.book.coupon.dto.MemberCouponResponse;
import shop.nuribooks.books.book.coupon.entity.MemberCoupon;

public interface MemberCouponRepositoryCustom {
	List<MemberCouponResponse> findAllByMemberId(Long memberId);

	Page<MemberCouponResponse> findAvailableCouponsByMemberId(Long memberId, Pageable pageable);

	Page<MemberCouponResponse> findExpiredOrUsedCouponsByMemberId(Long memberId, Pageable pageable);

	List<MemberCouponOrderDto> findAllTypeAvailableCouponsByMemberId(Long memberId, BigDecimal orderTotalPrice);

	List<MemberCoupon> findAllUsableCouponsByMemberId(Long memberId);
}
