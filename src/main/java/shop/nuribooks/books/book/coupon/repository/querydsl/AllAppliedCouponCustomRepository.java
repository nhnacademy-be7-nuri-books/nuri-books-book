package shop.nuribooks.books.book.coupon.repository.querydsl;

import shop.nuribooks.books.book.coupon.dto.CouponAppliedOrderDto;

public interface AllAppliedCouponCustomRepository {
	CouponAppliedOrderDto findAppliedCouponInfo(Long orderId);
}
