package shop.nuribooks.books.book.coupon.strategy;

import shop.nuribooks.books.book.coupon.dto.CouponRequest;
import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.entity.CouponPolicy;

public class AllCouponStrategy implements CouponStrategy {
	@Override
	public Coupon registerCoupon(CouponRequest request, CouponPolicy couponPolicy) {
		return Coupon.builder()
			.name(request.name())
			.couponType(request.couponType())
			.couponPolicy(couponPolicy) // CouponPolicy 매핑
			.expirationType(request.expirationType())
			.expiredAt(request.expiredAt())
			.period(request.period())
			.issuanceType(request.issuanceType())
			.quantity(request.quantity())
			.build();
	}
}
