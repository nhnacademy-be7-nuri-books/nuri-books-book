package shop.nuribooks.books.book.coupon.enums;

import shop.nuribooks.books.book.coupon.strategy.coupontype.AllCouponStrategy;
import shop.nuribooks.books.book.coupon.strategy.coupontype.BookCouponStrategy;
import shop.nuribooks.books.book.coupon.strategy.coupontype.CategoryCouponStrategy;
import shop.nuribooks.books.book.coupon.strategy.coupontype.CouponTypeStrategy;

public enum CouponType {
	ALL("전체", new AllCouponStrategy()),
	BOOK("도서", new BookCouponStrategy()),
	CATEGORY("카테고리", new CategoryCouponStrategy());

	public final CouponTypeStrategy couponTypeStrategy;
	private final String korName;

	CouponType(String korName, CouponTypeStrategy couponTypeStrategy) {
		this.korName = korName;
		this.couponTypeStrategy = couponTypeStrategy;
	}

	@Override
	public String toString() {
		return this.korName;
	}

}
