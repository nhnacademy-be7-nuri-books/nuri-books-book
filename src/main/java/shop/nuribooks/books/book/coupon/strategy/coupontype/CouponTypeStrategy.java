package shop.nuribooks.books.book.coupon.strategy.coupontype;

public interface CouponTypeStrategy {
	boolean isApplicable(Long id, Long targetId);
}
