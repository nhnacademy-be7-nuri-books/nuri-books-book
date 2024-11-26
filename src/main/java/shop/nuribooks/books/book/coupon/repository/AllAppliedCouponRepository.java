package shop.nuribooks.books.book.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.coupon.entity.AllAppliedCoupon;
import shop.nuribooks.books.book.coupon.repository.querydsl.AllAppliedCouponCustomRepository;

public interface AllAppliedCouponRepository
	extends AllAppliedCouponCustomRepository, JpaRepository<AllAppliedCoupon, Long> {
}
