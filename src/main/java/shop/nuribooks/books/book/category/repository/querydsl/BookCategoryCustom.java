package shop.nuribooks.books.book.category.repository.querydsl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.book.dto.AdminBookListResponse;
import shop.nuribooks.books.book.category.dto.SimpleCategoryResponse;

public interface BookCategoryCustom {
	//도서 id로 Category 목록 조회
	List<List<SimpleCategoryResponse>> findCategoriesByBookId(Long bookId);

	List<AdminBookListResponse> findBooksByCategoryId(List<Long> categoryIds, Pageable pageable);

	Page<AdminBookListResponse> findBooksByCategoryIdWithPaging(List<Long> categoryIds, Pageable pageable);

	long countBookByCategoryIds(List<Long> categoryIds);
}
