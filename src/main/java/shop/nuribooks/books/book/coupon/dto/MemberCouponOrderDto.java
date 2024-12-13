package shop.nuribooks.books.book.coupon.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import shop.nuribooks.books.book.coupon.entity.MemberCoupon;
import shop.nuribooks.books.book.coupon.enums.DiscountType;

@Builder
public record MemberCouponOrderDto(
	@NotNull long couponId,
	@NotNull long memberCouponId,
	@NotNull String couponName,
	@NotNull DiscountType discountType,
	@NotNull int discount,
	@NotNull BigDecimal minimumOrderPrice,
	@NotNull BigDecimal maximumDiscountPrice,
	@NotNull boolean isUsed,
	@NotNull LocalDate createdAt,
	@NotNull LocalDate expiredAt,
	@NotNull BigDecimal discountPrice,
	List<Long> bookIds) {

	public static MemberCouponOrderDto toDto(MemberCoupon memberCoupon, BigDecimal totalPrice, List<Long> bookIds) {
		return MemberCouponOrderDto.builder()
			.couponId(memberCoupon.getCoupon().getId())
			.memberCouponId(memberCoupon.getId())
			.couponName(memberCoupon.getCoupon().getName())
			.discountType(memberCoupon.getCoupon().getCouponPolicy().getDiscountType())
			.discount(memberCoupon.getCoupon().getCouponPolicy().getDiscount())
			.minimumOrderPrice(memberCoupon.getCoupon().getCouponPolicy().getMinimumOrderPrice())
			.maximumDiscountPrice(memberCoupon.getCoupon().getCouponPolicy().getMaximumDiscountPrice())
			.isUsed(memberCoupon.isUsed())
			.createdAt(memberCoupon.getCreatedAt())
			.expiredAt(memberCoupon.getExpiredAt())
			.discountPrice(memberCoupon.getCoupon().getCouponPolicy().calculateDiscount(totalPrice))
			.bookIds(bookIds != null ? bookIds : List.of())
			.build();
	}

	public static MemberCouponOrderDto toDto(MemberCoupon memberCoupon, BigDecimal totalPrice) {
		return toDto(memberCoupon, totalPrice, null);
	}

}
