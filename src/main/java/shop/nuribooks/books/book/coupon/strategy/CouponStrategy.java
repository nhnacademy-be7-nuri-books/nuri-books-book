package shop.nuribooks.books.book.coupon.strategy;

import java.util.List;

import shop.nuribooks.books.book.book.dto.response.BookOrderResponse;
import shop.nuribooks.books.book.coupon.dto.CouponRequest;
import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.entity.CouponPolicy;

public interface CouponStrategy {
	Coupon registerCoupon(CouponRequest request, CouponPolicy couponPolicy);

	boolean isCouponApplicableToOrder(Coupon coupon, List<BookOrderResponse> bookOrderResponses);
}

