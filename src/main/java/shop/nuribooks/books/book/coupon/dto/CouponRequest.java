package shop.nuribooks.books.book.coupon.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.enums.CouponType;
import shop.nuribooks.books.book.coupon.enums.ExpirationType;
import shop.nuribooks.books.book.coupon.enums.IssuanceType;
import shop.nuribooks.books.book.point.enums.PolicyType;

public record CouponRequest(

	@NotNull(message = "이름은 필수입니다.")
	@Size(min = 2, max = 50)
	String name,

	@NotNull(message = "쿠폰 할인 유형은 필수입니다.")
	PolicyType policyType,

	@NotNull(message = "할인 할당량은 필수입니다.")
	@Min(value = 1, message = "할인 할당량은 필수입니다.")
	int discount,

	@NotNull(message = "최소 주문 금액은 필수입니다.")
	@Digits(integer = 10, fraction = 0)
	@PositiveOrZero(message = "최소 주문 금액은 0 이상이어야 합니다.")
	BigDecimal minimumOrderPrice,

	@NotNull(message = "최대 할인 금액은 필수입니다.")
	@Digits(integer = 10, fraction = 0)
	@Min(value = 1, message = "최대 할인 금액은 1원 이상이어야 합니다.")
	BigDecimal maximumDiscountPrice,

	@NotNull(message = "시작일시는 필수입니다.")
	LocalDate createdAt,

	LocalDate expiredAt,

	@Min(value = 1, message = "할인 기간은 1일 이상이어야 합니다.")
	Integer period,

	@NotNull(message = "만료유형은 필수입니다.") //기간 쿠폰, 만료일 쿠폰
	ExpirationType expirationType,

	@NotNull(message = "쿠폰유형은 필수입니다.") //전체 적용, 도서 적용, 카테고리 적용
	CouponType couponType,

	@NotNull(message = "수량 제한 여부는 필수 입니다.")
	IssuanceType issuanceType,

	int quantity
) {
	public Coupon toEntity() {
		return Coupon.builder()
			.name(name)
			.couponType(couponType)
			.policyType(policyType)
			.discount(discount)
			.minimumOrderPrice(minimumOrderPrice)
			.maximumDiscountPrice(maximumDiscountPrice)
			.expirationType(expirationType)
			.period(period)
			.expiredAt(expiredAt)
			.issuanceType(issuanceType)
			.quantity(quantity)
			.build();

	}
}
