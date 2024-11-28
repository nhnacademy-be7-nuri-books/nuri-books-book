package shop.nuribooks.books.book.coupon.strategy.coupontype;

public class BookCouponStrategy implements CouponTypeStrategy {
	@Override
	public boolean isApplicable(Long id, Long targetId) {
		return id.equals(targetId);
	}
}
