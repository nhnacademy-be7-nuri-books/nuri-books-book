package shop.nuribooks.books.book.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.coupon.entity.CouponPolicy;

public interface CouponPolicyRepository extends JpaRepository<CouponPolicy, Long> {
	boolean existsByNameIgnoreCase(String name);
}
