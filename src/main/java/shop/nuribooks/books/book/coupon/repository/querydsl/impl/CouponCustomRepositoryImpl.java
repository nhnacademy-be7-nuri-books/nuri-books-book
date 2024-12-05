package shop.nuribooks.books.book.coupon.repository.querydsl.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.coupon.dto.CouponResponse;
import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.entity.QCoupon;
import shop.nuribooks.books.book.coupon.enums.CouponType;
import shop.nuribooks.books.book.coupon.mapper.CouponMapper;
import shop.nuribooks.books.book.coupon.repository.querydsl.CouponCustomRepository;

@RequiredArgsConstructor
public class CouponCustomRepositoryImpl implements CouponCustomRepository {
	private final JPAQueryFactory queryFactory;
	private final CouponMapper couponMapper;

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
	public Page<CouponResponse> findCouponsByCouponType(Pageable pageable, CouponType type) {
		QCoupon coupon = QCoupon.coupon;

		List<Coupon> coupons = queryFactory.selectFrom(coupon)
			.where(coupon.couponType.eq(type))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// 엔티티 -> DTO 변환
		List<CouponResponse> results = coupons.stream()
			.map(couponMapper::toDto) // CouponMapper 활용
			.toList();

		// 총 개수 조회
		long total = queryFactory.select(coupon.count())
			.from(coupon)
			.where(coupon.couponType.eq(type))
			.fetchOne();

		return new PageImpl<>(results, pageable, total);
	}

}
