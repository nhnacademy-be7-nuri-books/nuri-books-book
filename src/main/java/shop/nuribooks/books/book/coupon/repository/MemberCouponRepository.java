package shop.nuribooks.books.book.coupon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.coupon.dto.MemberCouponResponse;
import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.entity.MemberCoupon;
import shop.nuribooks.books.book.coupon.repository.querydsl.MemberCouponRepositoryCustom;
import shop.nuribooks.books.member.member.entity.Member;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long>, MemberCouponRepositoryCustom {
	List<MemberCouponResponse> findAllByMemberId(Long memberId);

	boolean existsByMemberId(Long memberId);

	boolean existsByMemberAndCoupon(Member member, Coupon coupon);

}
