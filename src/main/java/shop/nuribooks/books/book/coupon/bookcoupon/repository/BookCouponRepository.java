package shop.nuribooks.books.book.coupon.bookcoupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.coupon.bookcoupon.entity.BookCoupon;

public interface BookCouponRepository extends JpaRepository<BookCoupon, Long> {
}
