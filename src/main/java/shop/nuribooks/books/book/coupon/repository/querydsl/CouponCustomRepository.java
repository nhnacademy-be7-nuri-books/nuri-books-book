package shop.nuribooks.books.book.coupon.repository.querydsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.enums.CouponType;

public interface CouponCustomRepository {
	Coupon findCouponsByNameLike(String name);

	Page<Coupon> findCouponsByCouponType(Pageable pageable, CouponType type);
}
