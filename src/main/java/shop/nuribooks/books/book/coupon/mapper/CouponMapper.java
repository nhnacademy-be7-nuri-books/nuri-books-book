package shop.nuribooks.books.book.coupon.mapper;

import org.springframework.stereotype.Component;

import shop.nuribooks.books.book.coupon.dto.CouponResponse;
import shop.nuribooks.books.book.coupon.entity.AllCoupon;
import shop.nuribooks.books.book.coupon.entity.BookCoupon;
import shop.nuribooks.books.book.coupon.entity.CategoryCoupon;
import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.enums.CouponType;

@Component
public class CouponMapper {

	public CouponResponse toDto(Coupon coupon) {
		if (coupon.getCouponType().equals(CouponType.BOOK)) {
			return mapBookCoupon((BookCoupon)coupon);
		} else if (coupon.getCouponType().equals(CouponType.CATEGORY)) {
			return mapCategoryCoupon((CategoryCoupon)coupon);
		} else if (coupon.getCouponType().equals(CouponType.ALL)) {
			return mapAllCoupon((AllCoupon)coupon);
		}
		throw new IllegalArgumentException("Unsupported coupon type: " + coupon.getClass().getSimpleName());
	}

	private CouponResponse mapAllCoupon(AllCoupon allCoupon) {
		return CouponResponse.builder()
			.id(allCoupon.getId())
			.name(allCoupon.getName())
			.couponType(allCoupon.getCouponType())
			.discountType(allCoupon.getCouponPolicy().getDiscountType())
			.discount(allCoupon.getCouponPolicy().getDiscount())
			.minimumOrderPrice(allCoupon.getCouponPolicy().getMinimumOrderPrice())
			.maximumDiscountPrice(allCoupon.getCouponPolicy().getMaximumDiscountPrice())
			.expirationType(allCoupon.getExpirationType())
			.expiredAt(allCoupon.getExpiredAt())
			.period(allCoupon.getPeriod())
			.issuanceType(allCoupon.getIssuanceType())
			.quantity(allCoupon.getQuantity())
			.createdAt(allCoupon.getCreatedAt())
			.deletedAt(allCoupon.getDeletedAt())
			.build();
	}

	private CouponResponse mapBookCoupon(BookCoupon bookCoupon) {
		return CouponResponse.builder()
			.id(bookCoupon.getId())
			.name(bookCoupon.getName())
			.couponType(bookCoupon.getCouponType())
			.discountType(bookCoupon.getCouponPolicy().getDiscountType())
			.discount(bookCoupon.getCouponPolicy().getDiscount())
			.minimumOrderPrice(bookCoupon.getCouponPolicy().getMinimumOrderPrice())
			.maximumDiscountPrice(bookCoupon.getCouponPolicy().getMaximumDiscountPrice())
			.expirationType(bookCoupon.getExpirationType())
			.expiredAt(bookCoupon.getExpiredAt())
			.period(bookCoupon.getPeriod())
			.issuanceType(bookCoupon.getIssuanceType())
			.quantity(bookCoupon.getQuantity())
			.itemId(bookCoupon.getBook().getId())
			.itemName(bookCoupon.getBook().getTitle())
			.createdAt(bookCoupon.getCreatedAt())
			.deletedAt(bookCoupon.getDeletedAt())
			.build();
	}

	private CouponResponse mapCategoryCoupon(CategoryCoupon categoryCoupon) {
		return CouponResponse.builder()
			.id(categoryCoupon.getId())
			.name(categoryCoupon.getName())
			.couponType(categoryCoupon.getCouponType())
			.discountType(categoryCoupon.getCouponPolicy().getDiscountType())
			.discount(categoryCoupon.getCouponPolicy().getDiscount())
			.minimumOrderPrice(categoryCoupon.getCouponPolicy().getMinimumOrderPrice())
			.maximumDiscountPrice(categoryCoupon.getCouponPolicy().getMaximumDiscountPrice())
			.expirationType(categoryCoupon.getExpirationType())
			.expiredAt(categoryCoupon.getExpiredAt())
			.period(categoryCoupon.getPeriod())
			.issuanceType(categoryCoupon.getIssuanceType())
			.quantity(categoryCoupon.getQuantity())
			.itemId(categoryCoupon.getCategory().getId())
			.itemName(categoryCoupon.getCategory().getName())
			.createdAt(categoryCoupon.getCreatedAt())
			.deletedAt(categoryCoupon.getDeletedAt())
			.build();
	}
}

