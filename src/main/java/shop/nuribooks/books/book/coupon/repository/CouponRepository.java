package shop.nuribooks.books.book.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.repository.querydsl.CouponCustomRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long>, CouponCustomRepository {
	boolean existsByNameIgnoreCaseAndDeletedAtIsNull(String name);
}
