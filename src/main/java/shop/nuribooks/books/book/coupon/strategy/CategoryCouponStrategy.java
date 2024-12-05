package shop.nuribooks.books.book.coupon.strategy;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.category.entity.Category;
import shop.nuribooks.books.book.category.repository.CategoryRepository;
import shop.nuribooks.books.book.coupon.dto.CouponRequest;
import shop.nuribooks.books.book.coupon.entity.CategoryCoupon;
import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.entity.CouponPolicy;
import shop.nuribooks.books.exception.category.CategoryNotFoundException;

@Component
@RequiredArgsConstructor
public class CategoryCouponStrategy implements CouponStrategy {

	private final CategoryRepository categoryRepository;

	@Override
	public Coupon registerCoupon(CouponRequest request, CouponPolicy couponPolicy) {
		// Category 엔티티 조회
		Category category = categoryRepository.findById(request.itemId())
			.orElseThrow(() -> new CategoryNotFoundException("Invalid category ID"));

		// CategoryCoupon 생성
		return CategoryCoupon.builder()
			.name(request.name())
			.couponType(request.couponType())
			.couponPolicy(couponPolicy)
			.expirationType(request.expirationType())
			.period(request.period())
			.issuanceType(request.issuanceType())
			.quantity(request.quantity())
			.category(category) // 연관된 Category 설정
			.build();
	}
}
