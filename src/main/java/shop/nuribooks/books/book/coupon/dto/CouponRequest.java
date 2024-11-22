package shop.nuribooks.books.book.coupon.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.enums.CouponType;
import shop.nuribooks.books.book.coupon.enums.ExpirationType;
import shop.nuribooks.books.book.point.enums.PolicyType;

public record CouponRequest(

	@NotNull(message = "이름은 필수입니다.")
	@Size(min = 2, max = 50)
	String name,

	@NotNull(message = "쿠폰 할인 유형은 필수입니다.")
	PolicyType policyType,

	@NotNull(message = "할인 할당량은 필수입니다.")
	@PositiveOrZero
	int discount,

	@NotNull(message = "최소 주문 금액은 필수입니다.")
	@Digits(integer = 10, fraction = 0)
	BigDecimal minimumOrderPrice,

	@NotNull(message = "최대 할인 금액은 필수입니다.")
	@Digits(integer = 10, fraction = 0)
	BigDecimal maximumDiscountPrice,

	@NotNull(message = "시작일시는 필수입니다.")
	LocalDate createdAt,

	LocalDate expiredAt,

	Integer period,

	@NotNull(message = "만료유형은 필수입니다.") //기간 쿠폰, 만료일 쿠폰
	ExpirationType expirationType,

	LocalDateTime expiredDate,
	@NotNull(message = "쿠폰유형은 필수입니다.") //전체 적용, 도서 적용, 카테고리 적용
	CouponType couponType) {
	public Coupon toEntity() {
		return Coupon.builder()
			.name(name)
			.policyType(policyType)
			.discount(discount)
			.minimumOrderPrice(minimumOrderPrice)
			.maximumDiscountPrice(maximumDiscountPrice)
			.createdAt(createdAt)
			.expiredAt(expiredAt)
			.expirationType(expirationType)
			.period(period != null ? period : 0)
			.expiredDate(expiredDate)
			.couponType(couponType)
			.build();

	}
}
