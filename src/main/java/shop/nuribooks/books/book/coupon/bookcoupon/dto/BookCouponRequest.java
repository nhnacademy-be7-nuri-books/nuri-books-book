package shop.nuribooks.books.book.coupon.bookcoupon.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.coupon.bookcoupon.entity.BookCoupon;
import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.enums.CouponType;
import shop.nuribooks.books.book.coupon.enums.ExpirationType;

@Builder
public record BookCouponRequest(
	@NotNull(message = "이름은 필수입니다.")
	@Size(min = 2, max = 50)
	String name,

	@NotNull(message = "쿠폰 할인 유형은 필수입니다.")
	Long couponPolicyId,

	@NotNull(message = "시작일시는 필수입니다.")
	LocalDate createdAt,

	LocalDate expiredAt,

	@Min(value = 1, message = "할인 기간은 1일 이상이어야 합니다.")
	Integer period,

	@NotNull(message = "만료유형은 필수입니다.") //기간 쿠폰, 만료일 쿠폰
	ExpirationType expirationType,

	LocalDateTime expiredDate,
	@NotNull(message = "쿠폰유형은 필수입니다.") //전체 적용, 도서 적용, 카테고리 적용
	CouponType couponType,

	@NotNull(message = "도서 id는 필수입니다.")
	Long bookId
) {
	public BookCoupon toEntity(Coupon coupon, Book book) {
		return BookCoupon.builder()
			.coupon(coupon)
			.book(book)
			.build();
	}
}
