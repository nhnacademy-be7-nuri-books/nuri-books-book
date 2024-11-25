package shop.nuribooks.books.book.coupon.repository.querydsl.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.coupon.dto.CouponResponse;
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
				.and(coupon.expiredDate.isNull()))
			.fetchFirst();
	}

	@Override
	public Page<CouponResponse> findCouponsByCouponId(Pageable pageable, CouponType type) {
		QCoupon coupon = QCoupon.coupon;

		QueryResults<CouponResponse> results = queryFactory.select(Projections.constructor(CouponResponse.class,
				coupon.id,
				coupon.policyType,
				coupon.name,
				coupon.discount,
				coupon.minimumOrderPrice,
				coupon.maximumDiscountPrice,
				coupon.createdAt,
				coupon.expiredAt,
				coupon.period,
				coupon.expirationType,
				coupon.expiredDate,
				coupon.couponType
			))
			.from(coupon)
			.where(coupon.couponType.eq(type))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetchResults();

		List<CouponResponse> coupons = results.getResults();
		long total = results.getTotal();

		return new PageImpl<>(coupons, pageable, total);
	}

}
