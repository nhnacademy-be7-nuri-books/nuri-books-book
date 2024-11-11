package shop.nuribooks.books.book.category.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.book.dto.BookContributorsResponse;
import shop.nuribooks.books.book.category.dto.SimpleCategoryResponse;
import shop.nuribooks.books.common.message.PagedResponse;

public interface BookCategoryService {
	void registerBookCategory(Long bookId, Long categoryId);

	void deleteBookCategory(Long bookId, Long categoryId);

	List<List<SimpleCategoryResponse>> findCategoriesByBookId(Long bookId);

	PagedResponse<BookContributorsResponse> findBooksByCategoryId(Long categoryId, Pageable pageable);
}
