package shop.nuribooks.books.book.coupon.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.coupon.entity.AllAppliedCoupon;
import shop.nuribooks.books.book.coupon.repository.querydsl.AllAppliedCouponCustomRepository;
import shop.nuribooks.books.order.order.entity.Order;

public interface AllAppliedCouponRepository
	extends AllAppliedCouponCustomRepository, JpaRepository<AllAppliedCoupon, Long> {
	Optional<AllAppliedCoupon> findByOrder(Order order);
}
