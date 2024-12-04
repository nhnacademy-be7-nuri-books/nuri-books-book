package shop.nuribooks.books.book.coupon.categorycoupon.repository;

import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.coupon.categorycoupon.entity.CategoryCoupon;
import shop.nuribooks.books.book.coupon.categorycoupon.entity.QCategoryCoupon;

@RequiredArgsConstructor
public class CategoryCouponCustomRepositoryImpl implements CategoryCouponCustomRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<CategoryCoupon> findByCategoryId(Long categoryId) {
		QCategoryCoupon categoryCoupon = QCategoryCoupon.categoryCoupon;

		CategoryCoupon result = queryFactory
			.selectFrom(categoryCoupon)
			.where(categoryCoupon.category.id.eq(categoryId)
				.and(categoryCoupon.coupon.deletedAt.isNull()))
			.fetchFirst();

		return Optional.ofNullable(result);
	}
}
