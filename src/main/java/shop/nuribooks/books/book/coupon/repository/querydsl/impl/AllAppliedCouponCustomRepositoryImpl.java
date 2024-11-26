package shop.nuribooks.books.book.coupon.repository.querydsl.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.coupon.dto.CouponAppliedOrderDto;
import shop.nuribooks.books.book.coupon.entity.QAllAppliedCoupon;
import shop.nuribooks.books.book.coupon.entity.QCoupon;
import shop.nuribooks.books.book.coupon.entity.QMemberCoupon;
import shop.nuribooks.books.book.coupon.repository.querydsl.AllAppliedCouponCustomRepository;

@RequiredArgsConstructor
public class AllAppliedCouponCustomRepositoryImpl implements AllAppliedCouponCustomRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public CouponAppliedOrderDto findAppliedCouponInfo(Long orderId) {

		QAllAppliedCoupon allAppliedCoupon = QAllAppliedCoupon.allAppliedCoupon;
		QMemberCoupon memberCoupon = QMemberCoupon.memberCoupon;
		QCoupon coupon = QCoupon.coupon;

		return queryFactory.select(Projections.constructor(CouponAppliedOrderDto.class,
				coupon.name,
				allAppliedCoupon.discount_price,
				coupon.couponType
			))
			.from(allAppliedCoupon)
			.join(memberCoupon)
			.on(allAppliedCoupon.id.eq(memberCoupon.coupon.id))
			.join(coupon)
			.on(memberCoupon.coupon.id.eq(coupon.id))
			.where(allAppliedCoupon.order.id.eq(orderId))
			.fetchOne();
	}
}
