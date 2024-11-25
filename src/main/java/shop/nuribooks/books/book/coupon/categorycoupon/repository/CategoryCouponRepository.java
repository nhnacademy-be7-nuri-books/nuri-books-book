package shop.nuribooks.books.book.coupon.categorycoupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.coupon.categorycoupon.entity.CategoryCoupon;

public interface CategoryCouponRepository extends JpaRepository<CategoryCoupon, Long> {
}
