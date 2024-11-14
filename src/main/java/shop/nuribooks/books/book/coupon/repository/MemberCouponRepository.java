package shop.nuribooks.books.book.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.coupon.entity.MemberCoupon;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {
}
