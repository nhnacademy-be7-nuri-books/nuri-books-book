package shop.nuribooks.books.book.coupon.strategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.dto.response.BookOrderResponse;
import shop.nuribooks.books.book.category.entity.BookCategory;
import shop.nuribooks.books.book.category.entity.Category;
import shop.nuribooks.books.book.category.repository.BookCategoryRepository;
import shop.nuribooks.books.book.category.repository.CategoryRepository;
import shop.nuribooks.books.book.coupon.dto.CouponRequest;
import shop.nuribooks.books.book.coupon.dto.MemberCouponOrderDto;
import shop.nuribooks.books.book.coupon.entity.CategoryCoupon;
import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.entity.CouponPolicy;
import shop.nuribooks.books.book.coupon.entity.MemberCoupon;
import shop.nuribooks.books.exception.category.CategoryNotFoundException;

@Component
@RequiredArgsConstructor
public class CategoryCouponStrategy implements CouponStrategy {

	private final CategoryRepository categoryRepository;
	private final BookCategoryRepository bookCategoryRepository;

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

	@Override
	public MemberCouponOrderDto isCouponApplicableToOrder(MemberCoupon memberCoupon,
		List<BookOrderResponse> bookOrderResponses) {
		BigDecimal totalOrderPrice = BigDecimal.ZERO;

		CategoryCoupon categoryCoupon = (CategoryCoupon)memberCoupon.getCoupon();
		List<Long> bookIds = new ArrayList<>();

		List<Long> applicableCategoryIds = categoryRepository.findAllChildCategoryIds(
			categoryCoupon.getCategory().getId());

		for (BookOrderResponse bookOrderResponse : bookOrderResponses) {
			List<BookCategory> bookCategories = bookCategoryRepository.findByBookId(bookOrderResponse.bookId());

			for (BookCategory bookCategory : bookCategories) {
				if (applicableCategoryIds.contains(bookCategory.getCategory().getId())) {
					totalOrderPrice = totalOrderPrice.add(bookOrderResponse.bookTotalPrice());
					bookIds.add(bookOrderResponse.bookId());
					break;
				}
			}
		}
		if (totalOrderPrice.compareTo(categoryCoupon.getCouponPolicy().getMinimumOrderPrice()) >= 0) {
			return MemberCouponOrderDto.toDto(memberCoupon, totalOrderPrice, bookIds);
		}
		return null;
	}
}
