package shop.nuribooks.books.book.coupon.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.enums.ExpirationType;

public record CouponRequest(

	@NotNull(message = "이름은 필수입니다.")
	@Size(min = 3, max = 50)
	String name,

	@NotNull(message = "할인율은 필수입니다.")
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

	int period,

	@NotNull(message = "만료유형은 필수입니다.") //기간 쿠폰, 만료일 쿠폰
	ExpirationType expirationType) {

	public Coupon toEntity() {
		return Coupon.builder()
			.name(name)
			.discount(discount)
			.minimumOrderPrice(minimumOrderPrice)
			.maximumDiscountPrice(maximumDiscountPrice)
			.createdAt(createdAt)
			.expiredAt(expiredAt)
			.expirationType(expirationType)
			.period(period)
			.build();

	}
}
