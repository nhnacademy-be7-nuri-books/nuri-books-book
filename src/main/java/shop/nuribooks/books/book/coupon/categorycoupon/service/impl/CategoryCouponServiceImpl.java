package shop.nuribooks.books.book.coupon.categorycoupon.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.category.entity.Category;
import shop.nuribooks.books.book.category.repository.CategoryRepository;
import shop.nuribooks.books.book.coupon.categorycoupon.dto.CategoryCouponRequest;
import shop.nuribooks.books.book.coupon.categorycoupon.dto.CategoryCouponResponse;
import shop.nuribooks.books.book.coupon.categorycoupon.entity.CategoryCoupon;
import shop.nuribooks.books.book.coupon.categorycoupon.repository.CategoryCouponRepository;
import shop.nuribooks.books.book.coupon.categorycoupon.service.CategoryCouponService;
import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.repository.CouponRepository;
import shop.nuribooks.books.exception.category.CategoryNotFoundException;
import shop.nuribooks.books.exception.coupon.BookCouponNotFoundException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryCouponServiceImpl implements CategoryCouponService {

	private final CouponRepository couponRepository;
	private final CategoryRepository categoryRepository;
	private final CategoryCouponRepository categoryCouponRepository;

	@Transactional
	@Override
	public void registerCategoryCoupon(CategoryCouponRequest categoryCouponRequest) {
		Coupon coupon = Coupon.builder()
			.name(categoryCouponRequest.name())
			.policyType(categoryCouponRequest.policyType())
			.discount(categoryCouponRequest.discount())
			.minimumOrderPrice(categoryCouponRequest.minimumOrderPrice())
			.maximumDiscountPrice(categoryCouponRequest.maximumDiscountPrice())
			.expirationType(categoryCouponRequest.expirationType())
			.expiredAt(categoryCouponRequest.expiredAt())
			.period(categoryCouponRequest.period())
			.couponType(categoryCouponRequest.couponType())
			.build();

		couponRepository.saveAndFlush(coupon);
		Category category = categoryRepository.findById(categoryCouponRequest.categoryId())
			.orElseThrow(() -> new CategoryNotFoundException(categoryCouponRequest.categoryId()));

		categoryCouponRepository.save(CategoryCoupon.builder()
			.coupon(coupon)
			.category(category)
			.build());
	}

	@Override
	public CategoryCouponResponse getCategoryCoupon(Long categoryId) {
		CategoryCoupon categoryCoupon = categoryCouponRepository.findByCategoryId(categoryId)
			.orElseThrow(BookCouponNotFoundException::new);

		return new CategoryCouponResponse(categoryCoupon);
	}
}
