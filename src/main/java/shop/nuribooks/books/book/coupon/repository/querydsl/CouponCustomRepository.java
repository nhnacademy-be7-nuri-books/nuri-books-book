package shop.nuribooks.books.book.coupon.repository.querydsl;

import shop.nuribooks.books.book.coupon.entity.Coupon;

public interface CouponCustomRepository {
	Coupon findCouponsByNameLike(String name);

}
