package shop.nuribooks.books.book.category.service;

import java.util.List;

import shop.nuribooks.books.book.category.dto.SimpleCategoryResponse;

public interface BookCategoryService {
	void registerBookCategory(Long bookId, Long categoryId);

	void deleteBookCategory(Long bookId, Long categoryId);

	List<List<SimpleCategoryResponse>> findCategoriesByBookId(Long bookId);
}
