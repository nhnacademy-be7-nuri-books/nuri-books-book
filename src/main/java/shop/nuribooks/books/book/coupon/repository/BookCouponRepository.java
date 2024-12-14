package shop.nuribooks.books.book.coupon.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.coupon.entity.BookCoupon;

public interface BookCouponRepository extends JpaRepository<BookCoupon, Long> {
	Optional<BookCoupon> findByBookId(Long bookId);
}
