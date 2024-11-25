package shop.nuribooks.books.book.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.coupon.entity.AllAppliedCoupon;

public interface AllAppliedCouponRepository extends JpaRepository<AllAppliedCoupon, Long> {
}
