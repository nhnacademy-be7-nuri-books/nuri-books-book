package shop.nuribooks.books.book.coupon.repository.querydsl.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.entity.QCoupon;
import shop.nuribooks.books.book.coupon.repository.querydsl.CouponCustomRepository;
import shop.nuribooks.books.exception.coupon.CouponNotFoundException;

@RequiredArgsConstructor
public class CouponCustomRepositoryImpl implements CouponCustomRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public Coupon findCouponsByNameLike(String name) {
		QCoupon coupon = QCoupon.coupon;

		Coupon foundCoupon = queryFactory.selectFrom(coupon)
			.where(coupon.name.likeIgnoreCase(name)
				.or(coupon.name.likeIgnoreCase("%웰컴%"))
				.and(coupon.expireDate.isNull()))
			.fetchFirst();

		if (foundCoupon == null) {
			throw new CouponNotFoundException("조건에 맞는 쿠폰을 찾을 수 없습니다.");
		}

		return foundCoupon;
	}
}
