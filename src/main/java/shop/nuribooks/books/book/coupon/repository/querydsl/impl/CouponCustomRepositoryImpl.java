package shop.nuribooks.books.book.coupon.repository.querydsl.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.entity.QCoupon;
import shop.nuribooks.books.book.coupon.enums.CouponType;
import shop.nuribooks.books.book.coupon.repository.querydsl.CouponCustomRepository;

@RequiredArgsConstructor
public class CouponCustomRepositoryImpl implements CouponCustomRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public Coupon findCouponsByNameLike(String name) {
		QCoupon coupon = QCoupon.coupon;

		return queryFactory.selectFrom(coupon)
			.where(coupon.name.likeIgnoreCase(name)
				.or(coupon.name.likeIgnoreCase("%웰컴%"))
				.and(coupon.deletedAt.isNull()))
			.fetchFirst();
	}

	@Override
	public Page<Coupon> findCouponsByCouponType(Pageable pageable, CouponType type) {
		QCoupon coupon = QCoupon.coupon;

		List<Coupon> coupons = queryFactory.selectFrom(coupon)
			.where(coupon.couponType.eq(type))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = queryFactory.select(coupon.count())
			.from(coupon)
			.where(coupon.couponType.eq(type))
			.fetchOne();

		return new PageImpl<>(coupons, pageable, total);
	}

	@Override
	public int decrementCouponQuantity(Long couponId) {
		QCoupon coupon = QCoupon.coupon;

		return (int)queryFactory.update(coupon)
			.set(coupon.quantity, coupon.quantity.subtract(1))
			.where(coupon.id.eq(couponId)
				.and(coupon.quantity.gt(0)))
			.execute();
	}

}
