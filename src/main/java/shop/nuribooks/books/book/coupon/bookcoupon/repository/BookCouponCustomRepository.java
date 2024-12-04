package shop.nuribooks.books.book.coupon.bookcoupon.repository;

import java.util.Optional;

import shop.nuribooks.books.book.coupon.bookcoupon.entity.BookCoupon;

public interface BookCouponCustomRepository {
	Optional<BookCoupon> findByBookId(Long bookId);

}
