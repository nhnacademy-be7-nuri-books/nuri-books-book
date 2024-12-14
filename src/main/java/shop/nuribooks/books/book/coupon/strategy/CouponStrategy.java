package shop.nuribooks.books.book.coupon.strategy;

import java.math.BigDecimal;
import java.util.List;

import shop.nuribooks.books.book.book.dto.response.BookOrderResponse;
import shop.nuribooks.books.book.coupon.dto.CouponRequest;
import shop.nuribooks.books.book.coupon.dto.MemberCouponOrderDto;
import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.entity.CouponPolicy;
import shop.nuribooks.books.book.coupon.entity.MemberCoupon;

public interface CouponStrategy {
	Coupon registerCoupon(CouponRequest request, CouponPolicy couponPolicy);

	MemberCouponOrderDto isCouponApplicableToOrder(MemberCoupon memberCoupon,
		List<BookOrderResponse> bookOrderResponses);

	BigDecimal calculatePrice(MemberCoupon memberCoupon, List<BookOrderResponse> bookOrderResponses);
}

