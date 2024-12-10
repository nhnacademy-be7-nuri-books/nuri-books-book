package shop.nuribooks.books.book.coupon.repository.querydsl.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.coupon.dto.CouponAppliedOrderDto;
import shop.nuribooks.books.book.coupon.entity.QAllAppliedCoupon;
import shop.nuribooks.books.book.coupon.entity.QCoupon;
import shop.nuribooks.books.book.coupon.entity.QMemberCoupon;
import shop.nuribooks.books.book.coupon.repository.querydsl.AllAppliedCouponCustomRepository;
import shop.nuribooks.books.order.order.entity.QOrder;

@RequiredArgsConstructor
public class AllAppliedCouponCustomRepositoryImpl implements AllAppliedCouponCustomRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public CouponAppliedOrderDto findAppliedCouponInfo(Long orderId, Long id) {

		QAllAppliedCoupon allAppliedCoupon = QAllAppliedCoupon.allAppliedCoupon;
		QMemberCoupon memberCoupon = QMemberCoupon.memberCoupon;
		QCoupon coupon = QCoupon.coupon;
		QOrder order = QOrder.order;

		return queryFactory.select(Projections.constructor(CouponAppliedOrderDto.class,
				coupon.name,
				allAppliedCoupon.discountPrice,
				coupon.couponType
			))
			.from(allAppliedCoupon)
			.join(order)
			.on(allAppliedCoupon.order.id.eq(order.id))
			.join(memberCoupon)
			.on(order.customer.id.eq(memberCoupon.member.id))
			.join(coupon)
			.on(memberCoupon.coupon.id.eq(coupon.id))
			.where(allAppliedCoupon.order.id.eq(orderId)
				.and(allAppliedCoupon.id.eq(id)))
			.fetchOne();
	}
}
