package shop.nuribooks.books.book.coupon.enums;

public enum CouponType {
	ALL("전체"), BOOK("도서"), CATEGORY("카테고리");

	String korName;

	CouponType(String korName) {
		this.korName = korName;
	}

	public String toString() {
		return this.korName;
	}
}
