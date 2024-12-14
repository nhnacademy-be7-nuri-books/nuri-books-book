package shop.nuribooks.books.book.coupon.strategy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import shop.nuribooks.books.book.book.dto.response.BookOrderResponse;
import shop.nuribooks.books.book.coupon.dto.CouponRequest;
import shop.nuribooks.books.book.coupon.dto.MemberCouponOrderDto;
import shop.nuribooks.books.book.coupon.entity.AllCoupon;
import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.entity.CouponPolicy;
import shop.nuribooks.books.book.coupon.entity.MemberCoupon;

@Component
public class AllCouponStrategy implements CouponStrategy {
	@Override
	public Coupon registerCoupon(CouponRequest request, CouponPolicy couponPolicy) {
		return AllCoupon.builder()
			.name(request.name())
			.couponType(request.couponType())
			.couponPolicy(couponPolicy) // CouponPolicy 매핑
			.expirationType(request.expirationType())
			.expiredAt(request.expiredAt())
			.period(request.period())
			.issuanceType(request.issuanceType())
			.quantity(request.quantity())
			.createdAt(LocalDate.now())
			.build();
	}

	@Override
	public MemberCouponOrderDto isCouponApplicableToOrder(MemberCoupon memberCoupon,
		List<BookOrderResponse> bookOrderResponses) {
		BigDecimal totalOrderPrice = BigDecimal.ZERO;

		for (BookOrderResponse bookOrderResponse : bookOrderResponses) {
			totalOrderPrice = totalOrderPrice.add(bookOrderResponse.bookTotalPrice());
		}

		if (totalOrderPrice.compareTo(memberCoupon.getCoupon().getCouponPolicy().getMinimumOrderPrice()) >= 0) {
			return MemberCouponOrderDto.toDto(memberCoupon, totalOrderPrice);
		}
		return null;
	}

	@Override
	public BigDecimal calculatePrice(MemberCoupon memberCoupon, List<BookOrderResponse> bookOrderResponses) {
		BigDecimal totalOrderPrice = BigDecimal.ZERO;

		for (BookOrderResponse bookOrderResponse : bookOrderResponses) {
			totalOrderPrice = totalOrderPrice.add(bookOrderResponse.bookTotalPrice());
		}

		if (totalOrderPrice.compareTo(memberCoupon.getCoupon().getCouponPolicy().getMinimumOrderPrice()) >= 0) {
			return memberCoupon.getCoupon().getCouponPolicy().calculateDiscount(totalOrderPrice);
		}
		return BigDecimal.ZERO;
	}
}
