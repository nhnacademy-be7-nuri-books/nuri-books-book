package shop.nuribooks.books.book.coupon.strategy.coupontype;

public class AllCouponStrategy implements CouponTypeStrategy {
	
	@Override
	public boolean isApplicable(Long id, Long targetId) {
		return true;
	}
}
