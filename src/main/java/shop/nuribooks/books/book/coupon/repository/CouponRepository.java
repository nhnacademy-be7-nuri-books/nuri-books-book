package shop.nuribooks.books.book.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.coupon.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
	boolean existsByNameIgnoreCase(String name);
}
