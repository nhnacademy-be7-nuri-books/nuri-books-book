package shop.nuribooks.books.book.coupon.strategy.coupontype;

import java.util.List;

import shop.nuribooks.books.book.category.repository.CategoryRepository;

public class CategoryCouponStrategy implements CouponTypeStrategy {

	CategoryRepository categoryRepository;

	@Override
	public boolean isApplicable(Long id, Long targetId) {
		List<Long> categoryIds = categoryRepository.findAllChildCategoryIds(id);

		return categoryIds.contains(targetId);

	}
}
