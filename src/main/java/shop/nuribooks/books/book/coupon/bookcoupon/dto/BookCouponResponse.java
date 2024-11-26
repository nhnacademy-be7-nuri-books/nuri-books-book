package shop.nuribooks.books.book.coupon.bookcoupon.dto;

import lombok.Builder;
import shop.nuribooks.books.book.coupon.bookcoupon.entity.BookCoupon;

@Builder
public record BookCouponResponse(
	Long couponId

) {
	public static BookCoupon of(BookCoupon bookCoupon) {
		return BookCoupon.builder()
			.coupon(bookCoupon.getCoupon())
			.book(bookCoupon.getBook())
			.build();
	}

	public BookCouponResponse(BookCoupon bookCoupon) {
		this(bookCoupon.getCoupon().getId());
	}
	}
