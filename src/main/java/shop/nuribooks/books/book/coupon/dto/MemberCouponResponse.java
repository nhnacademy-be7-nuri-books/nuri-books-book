package shop.nuribooks.books.book.coupon.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public record MemberCouponResponse(
	@NotNull Long id,
	@NotNull Long couponId,
	@NotNull Long memberId,
	@NotNull boolean isUsed,
	@NotNull @PastOrPresent LocalDate createdAt,
	@NotNull @FutureOrPresent LocalDate expiredAt) {
}
