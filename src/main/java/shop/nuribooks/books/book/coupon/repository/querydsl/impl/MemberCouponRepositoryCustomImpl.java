package shop.nuribooks.books.book.coupon.repository.querydsl.impl;

import static shop.nuribooks.books.book.coupon.entity.QMemberCoupon.*;
import static shop.nuribooks.books.book.coupon.enums.CouponType.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.coupon.dto.MemberCouponOrderDto;
import shop.nuribooks.books.book.coupon.dto.MemberCouponResponse;
import shop.nuribooks.books.book.coupon.entity.MemberCoupon;
import shop.nuribooks.books.book.coupon.entity.QMemberCoupon;
import shop.nuribooks.books.book.coupon.repository.querydsl.MemberCouponRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class MemberCouponRepositoryCustomImpl implements MemberCouponRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<MemberCouponResponse> findAllByMemberId(Long memberId) {
		QMemberCoupon memberCoupon = QMemberCoupon.memberCoupon;

		return queryFactory.select(Projections.constructor(MemberCouponResponse.class,
				memberCoupon.coupon.name,
				memberCoupon.coupon.couponPolicy.discountType,
				memberCoupon.coupon.couponPolicy.discount,
				memberCoupon.coupon.couponPolicy.minimumOrderPrice,
				memberCoupon.coupon.couponPolicy.maximumDiscountPrice,
				memberCoupon.isUsed,
				memberCoupon.createdAt,
				memberCoupon.expiredAt
			))
			.from(memberCoupon)
			.where(memberCoupon.member.id.eq(memberId))
			.fetch();
	}

	@Override
	public Page<MemberCouponResponse> findAvailableCouponsByMemberId(Long memberId, Pageable pageable) {
		List<MemberCouponResponse> availableCoupons = queryFactory
			.select(
				Projections.constructor(
					MemberCouponResponse.class,
					memberCoupon.coupon.name,
					memberCoupon.coupon.couponPolicy.discountType,
					memberCoupon.coupon.couponPolicy.discount,
					memberCoupon.coupon.couponPolicy.minimumOrderPrice,
					memberCoupon.coupon.couponPolicy.maximumDiscountPrice,
					memberCoupon.isUsed,
					memberCoupon.createdAt,
					memberCoupon.expiredAt
				)
			)
			.from(memberCoupon)
			.where(memberCoupon.member.id.eq(memberId)
				.and(memberCoupon.isUsed.eq(false))
				.and(memberCoupon.expiredAt.after(LocalDate.now())))
			.orderBy(memberCoupon.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = queryFactory
			.select(memberCoupon.count())
			.from(memberCoupon)
			.where(memberCoupon.member.id.eq(memberId)
				.and(memberCoupon.isUsed.eq(false).or(memberCoupon.expiredAt.after(LocalDate.now())))
			)
			.fetchOne();

		return new PageImpl<>(availableCoupons, pageable, total);
	}

	@Override
	public Page<MemberCouponResponse> findExpiredOrUsedCouponsByMemberId(Long memberId, Pageable pageable) {
		List<MemberCouponResponse> expiredCoupons = queryFactory
			.select(
				Projections.constructor(
					MemberCouponResponse.class,
					memberCoupon.coupon.name,
					memberCoupon.coupon.couponPolicy.discountType,
					memberCoupon.coupon.couponPolicy.discount,
					memberCoupon.coupon.couponPolicy.minimumOrderPrice,
					memberCoupon.coupon.couponPolicy.maximumDiscountPrice,
					memberCoupon.isUsed,
					memberCoupon.createdAt,
					memberCoupon.expiredAt
				)
			)
			.from(memberCoupon)
			.where(memberCoupon.member.id.eq(memberId)
				.and(memberCoupon.isUsed.eq(true).or(memberCoupon.expiredAt.loe(LocalDate.now())))
			)
			.orderBy(memberCoupon.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = queryFactory
			.select(memberCoupon.count())
			.from(memberCoupon)
			.where(memberCoupon.member.id.eq(memberId)
				.and(memberCoupon.isUsed.eq(true).or(memberCoupon.expiredAt.loe(LocalDate.now())))
			)
			.fetchOne();

		return new PageImpl<>(expiredCoupons, pageable, total);
	}

	@Override
	public List<MemberCouponOrderDto> findAllTypeAvailableCouponsByMemberId(Long memberId, BigDecimal orderTotalPrice) {
		return queryFactory
			.select(
				Projections.constructor(
					MemberCouponOrderDto.class,
					memberCoupon.coupon.id,
					memberCoupon.id,
					memberCoupon.coupon.name,
					memberCoupon.coupon.couponPolicy.discountType,
					memberCoupon.coupon.couponPolicy.discount,
					memberCoupon.coupon.couponPolicy.minimumOrderPrice,
					memberCoupon.coupon.couponPolicy.maximumDiscountPrice,
					memberCoupon.isUsed,
					memberCoupon.createdAt,
					memberCoupon.expiredAt
				)
			)
			.from(memberCoupon)
			.where(memberCoupon.member.id.eq(memberId)
				.and(memberCoupon.isUsed.eq(false))
				.and(memberCoupon.expiredAt.after(LocalDate.now()))
				.and(memberCoupon.coupon.couponType.eq(ALL))
				.and(memberCoupon.coupon.couponPolicy.minimumOrderPrice.lt(orderTotalPrice)))
			.orderBy(memberCoupon.createdAt.desc())
			.fetch();

	}

	@Override
	public List<MemberCoupon> findAllUsableCouponsByMemberId(Long memberId) {
		LocalDate currentDate = LocalDate.now();
		QMemberCoupon memberCoupon = QMemberCoupon.memberCoupon;
		return queryFactory.selectFrom(memberCoupon)
			.where(memberCoupon.member.id.eq(memberId)
				.and(memberCoupon.isUsed.eq(false))
				.and(memberCoupon.expiredAt.gt(currentDate)))
			.fetch();
	}
}
