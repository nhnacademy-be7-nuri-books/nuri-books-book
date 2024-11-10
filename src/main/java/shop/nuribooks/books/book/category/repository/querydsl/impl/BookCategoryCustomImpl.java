package shop.nuribooks.books.book.category.repository.querydsl.impl;

import static shop.nuribooks.books.book.category.entity.QBookCategory.*;
import static shop.nuribooks.books.book.category.entity.QCategory.*;

import java.util.ArrayList;
import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.category.dto.SimpleCategoryResponse;
import shop.nuribooks.books.book.category.entity.Category;
import shop.nuribooks.books.book.category.repository.querydsl.BookCategoryCustom;

@RequiredArgsConstructor
public class BookCategoryCustomImpl implements BookCategoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<List<SimpleCategoryResponse>> findCategoriesByBookId(Long bookId) {
		List<Category> categories = queryFactory.select(category)
			.from(bookCategory)
			.join(bookCategory.category, category)
			.where(bookCategory.book.id.eq(bookId))
			.fetch();

		List<List<SimpleCategoryResponse>> breadcrumbsList = new ArrayList<>();

		for (Category category : categories) {
			List<SimpleCategoryResponse> breadcrumbs = SimpleCategoryResponse.buildBreadcrumbs(category);
			breadcrumbsList.add(breadcrumbs);
		}

		return breadcrumbsList;
	}
}
