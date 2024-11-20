package shop.nuribooks.books.book.coupon.repository.querydsl.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.coupon.dto.MemberCouponResponse;
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
				memberCoupon.id,
				memberCoupon.coupon.id,
				memberCoupon.member.id,
				memberCoupon.isUsed,
				memberCoupon.createdAt,
				memberCoupon.expiredAt
			))
			.from(memberCoupon)
			.where(memberCoupon.member.id.eq(memberId))
			.fetch();
	}
}
