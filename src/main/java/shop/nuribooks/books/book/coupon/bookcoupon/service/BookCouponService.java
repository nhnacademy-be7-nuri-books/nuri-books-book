package shop.nuribooks.books.book.coupon.bookcoupon.service;

import shop.nuribooks.books.book.coupon.bookcoupon.dto.BookCouponRequest;
import shop.nuribooks.books.book.coupon.bookcoupon.entity.BookCoupon;

public interface BookCouponService {
	BookCoupon registerBookCoupon(BookCouponRequest request);

}
