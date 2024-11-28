package shop.nuribooks.books.book.coupon.categorycoupon.repository;

import java.util.Optional;

import shop.nuribooks.books.book.coupon.categorycoupon.entity.CategoryCoupon;

public interface CategoryCouponCustomRepository {
	Optional<CategoryCoupon> findByCategoryId(Long categoryId);

}
