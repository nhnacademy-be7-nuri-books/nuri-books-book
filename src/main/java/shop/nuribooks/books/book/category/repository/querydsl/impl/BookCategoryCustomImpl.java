package shop.nuribooks.books.book.category.repository.querydsl.impl;

import static shop.nuribooks.books.book.book.entity.QBook.*;
import static shop.nuribooks.books.book.category.entity.QBookCategory.*;
import static shop.nuribooks.books.book.category.entity.QCategory.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.dto.AdminBookListResponse;
import shop.nuribooks.books.book.book.dto.BookContributorsResponse;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.utility.BookUtils;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;
import shop.nuribooks.books.book.bookcontributor.repository.BookContributorRepository;
import shop.nuribooks.books.book.category.dto.SimpleCategoryResponse;
import shop.nuribooks.books.book.category.entity.Category;
import shop.nuribooks.books.book.category.repository.querydsl.BookCategoryCustom;

@Repository
@RequiredArgsConstructor
public class BookCategoryCustomImpl implements BookCategoryCustom {

	private final JPAQueryFactory queryFactory;
	private final BookContributorRepository bookContributorRepository;

	/**
	 * 주어진 도서 ID에 해당하는 카테고리 목록을 조회하고 각 카테고리에 대한 브레드크럼 목록을 반환합니다.
	 *
	 * @param bookId 조회할 도서의 ID
	 * @return 각 카테고리에 대한 브레드크럼을 포함한 목록의 목록
	 */
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

	@Override
	public List<AdminBookListResponse> findBooksByCategoryId(List<Long> categoryIds, Pageable pageable) {
		List<Book> books = queryFactory
			.select(book)
			.from(bookCategory)
			.join(bookCategory.book, book)
			.where(bookCategory.category.id.in(categoryIds))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
		return books.stream()
			.map(AdminBookListResponse::of)
			.toList();
	}

	@Override
	public Page<BookContributorsResponse> findBooksByCategoryIdWithPaging(List<Long> categoryIds, Pageable pageable) {
		List<Book> books = queryFactory
			.select(book)
			.from(bookCategory)
			.join(bookCategory.book, book)
			.where(bookCategory.category.id.in(categoryIds))
			.where(book.deletedAt.isNull())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// 총 개수를 계산할 때 NullPointerException을 방지하도록 수정합니다.
		Long totalElements = queryFactory
			.select(book.count())
			.from(bookCategory)
			.join(bookCategory.book, book)
			.where(bookCategory.category.id.in(categoryIds))
			.fetchOne();

		// totalElements가 null일 경우 기본값 0으로 설정합니다.
		totalElements = (totalElements != null) ? totalElements : 0L;

		// BookContributorsResponse 목록을 생성합니다.
		List<BookContributorsResponse> bookContributorsResponseList = books.stream()
			.map(book -> {
				AdminBookListResponse bookDetails = AdminBookListResponse.of(book);

				// 기여자 정보를 조회하여 역할별로 그룹화합니다.
				List<BookContributorInfoResponse> contributors = bookContributorRepository.findContributorsAndRolesByBookId(
					book.getId());
				Map<String, List<String>> contributorsByRole = BookUtils.groupContributorsByRole(contributors);

				// BookContributorsResponse 객체를 생성합니다.
				return new BookContributorsResponse(bookDetails, contributorsByRole);
			})
			.toList();
		return new PageImpl<>(bookContributorsResponseList, pageable, totalElements);
	}

	@Override
	public long countBookByCategoryIds(List<Long> categoryIds) {
		if (categoryIds == null || categoryIds.isEmpty()) {
			return 0L;
		}
		Long count = queryFactory
			.select(book.count())
			.from(bookCategory)
			.join(bookCategory.book, book)
			.where(bookCategory.category.id.in(categoryIds))
			.fetchOne();
		return count != null ? count : 0L;
	}
}
