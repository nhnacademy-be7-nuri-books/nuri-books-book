package shop.nuribooks.books.book.coupon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.coupon.dto.MemberCouponResponse;
import shop.nuribooks.books.book.coupon.entity.MemberCoupon;
import shop.nuribooks.books.book.coupon.repository.querydsl.MemberCouponRepositoryCustom;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long>, MemberCouponRepositoryCustom {
	List<MemberCouponResponse> findAllByMemberId(Long memberId);

	boolean existsByMemberId(Long memberId);
}
