package shop.nuribooks.books.book.coupon.categorycoupon.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import shop.nuribooks.books.book.coupon.enums.CouponType;
import shop.nuribooks.books.book.coupon.enums.ExpirationType;

public record CategoryCouponRequest(
	@NotNull(message = "이름은 필수입니다.")
	@Size(min = 2, max = 50)
	String name,

	@NotNull(message = "쿠폰 할인 유형은 필수입니다.")
	Long couponPolicyId,

	@NotNull(message = "시작일시는 필수입니다.")
	LocalDate createdAt,

	LocalDate expiredAt,

	Integer period,

	@NotNull(message = "만료유형은 필수입니다.") //기간 쿠폰, 만료일 쿠폰
	ExpirationType expirationType,

	LocalDateTime expiredDate,
	@NotNull(message = "쿠폰유형은 필수입니다.")
	CouponType couponType,

	@NotNull(message = "카테고리 id는 필수입니다.")
	Long categoryId
) {
}