package shop.nuribooks.books.book.coupon.bookcoupon.repository;

import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.coupon.bookcoupon.entity.BookCoupon;
import shop.nuribooks.books.book.coupon.bookcoupon.entity.QBookCoupon;

@RequiredArgsConstructor
public class BookCouponRepositoryImpl implements BookCouponCustomRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<BookCoupon> findByBookId(Long bookId) {
		QBookCoupon bookCoupon = QBookCoupon.bookCoupon;

		BookCoupon result = queryFactory
			.selectFrom(bookCoupon)
			.where(bookCoupon.book.id.eq(bookId)
				.and(bookCoupon.coupon.deletedAt.isNull()))
			.fetchOne();

		return Optional.ofNullable(result);
	}
}
