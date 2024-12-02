package shop.nuribooks.books.book.category.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.book.dto.BookContributorsResponse;
import shop.nuribooks.books.book.category.dto.SimpleCategoryResponse;

public interface BookCategoryService {
	void registerBookCategory(Long bookId, Long categoryId);

	void deleteBookCategory(Long bookId, Long categoryId);

	void deleteBookCategories(Long bookId);

	List<List<SimpleCategoryResponse>> findCategoriesByBookId(Long bookId);

	Page<BookContributorsResponse> findBooksByCategoryId(Long categoryId, Pageable pageable);
}
