package shop.nuribooks.books.book.category.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.dto.AdminBookListResponse;
import shop.nuribooks.books.book.book.dto.BookContributorsResponse;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.category.dto.SimpleCategoryResponse;
import shop.nuribooks.books.book.category.entity.BookCategory;
import shop.nuribooks.books.book.category.entity.Category;
import shop.nuribooks.books.book.category.repository.BookCategoryRepository;
import shop.nuribooks.books.book.category.repository.CategoryRepository;
import shop.nuribooks.books.book.category.service.BookCategoryService;
import shop.nuribooks.books.common.message.PagedResponse;
import shop.nuribooks.books.exception.InvalidPageRequestException;
import shop.nuribooks.books.exception.book.BookNotFoundException;
import shop.nuribooks.books.exception.category.BookCategoryAlreadyExistsException;
import shop.nuribooks.books.exception.category.BookCategoryNotFoundException;
import shop.nuribooks.books.exception.category.CategoryNotFoundException;

/**
 * 도서와 카테고리 간의 연관 관계를 관리하는 서비스 구현 클래스입니다.
 *
 * @author janghyun
 */
@RequiredArgsConstructor
@Service
public class BookCategoryServiceImpl implements BookCategoryService {
	private final BookRepository bookRepository;
	private final CategoryRepository categoryRepository;
	private final BookCategoryRepository bookCategoryRepository;

	/**
	 * 도서 ID와 카테고리 ID를 받아 두 객체를 연관시킵니다.
	 *
	 * @param bookId     연관시킬 도서의 ID
	 * @param categoryId 연관시킬 카테고리의 ID
	 * @throws BookNotFoundException     해당 ID의 도서를 찾을 수 없을 때 발생합니다.
	 * @throws CategoryNotFoundException 해당 ID의 카테고리를 찾을 수 없을 때 발생합니다.
	 */
	@Transactional
	@Override
	public void registerBookCategory(Long bookId, Long categoryId) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new BookNotFoundException(bookId));

		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(CategoryNotFoundException::new);

		boolean exists = bookCategoryRepository.existsByBookAndCategory(book, category);
		if (exists) {
			throw new BookCategoryAlreadyExistsException(bookId, categoryId);
		}

		bookCategoryRepository.save(BookCategory.builder()
			.book(book)
			.category(category)
			.build());
	}

	/**
	 * 도서 ID와 카테고리 ID를 받아 두 객체 간의 연관 관계를 삭제합니다.
	 *
	 * @param bookId     삭제할 도서의 ID
	 * @param categoryId 삭제할 카테고리의 ID
	 * @throws BookNotFoundException        해당 ID의 도서를 찾을 수 없을 때 발생합니다.
	 * @throws CategoryNotFoundException    해당 ID의 카테고리를 찾을 수 없을 때 발생합니다.
	 * @throws BookCategoryNotFoundException 해당 도서와 카테고리 간의 연관 관계가 없을 때 발생합니다.
	 */
	@Transactional
	@Override
	public void deleteBookCategory(Long bookId, Long categoryId) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new BookNotFoundException(bookId));

		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(CategoryNotFoundException::new);

		BookCategory bookCategory = bookCategoryRepository.findByBookAndCategory(book, category)
			.orElseThrow(() -> new BookCategoryNotFoundException(bookId, categoryId));

		bookCategoryRepository.delete(bookCategory);
	}

	/**
	 * 주어진 도서 ID에 해당하는 카테고리 목록을 조회하고 각 카테고리에 대한 브레드크럼 목록을 반환합니다.
	 *
	 * @param bookId 조회할 도서의 ID
	 * @return 각 카테고리에 대한 브레드크럼을 포함한 목록의 목록
	 * @throws BookNotFoundException 도서를 찾을 수 없는 경우 발생
	 */
	@Override
	public List<List<SimpleCategoryResponse>> findCategoriesByBookId(Long bookId) {
		if (!bookRepository.existsById(bookId)) {
			throw new BookNotFoundException(bookId);
		}

		return bookCategoryRepository.findCategoriesByBookId(bookId);
	}

	/**
	 * 주어진 카테고리 ID로 책을 페이지네이션하여 조회합니다.
	 *
	 * @param categoryId 조회할 카테고리의 ID
	 * @param pageable   페이지네이션 정보
	 * @return 책 목록이 포함된 페이지 응답
	 * @throws CategoryNotFoundException 지정된 카테고리 ID가 존재하지 않을 경우 발생
	 */
	@Override
	public PagedResponse<BookContributorsResponse> findBooksByCategoryId(Long categoryId, Pageable pageable) {
		if (!categoryRepository.existsById(categoryId)) {
			throw new CategoryNotFoundException();
		}

		if (pageable.getPageNumber() < 0) {
			throw new InvalidPageRequestException("페이지 번호는 0 이상이어야 합니다.");
		}

		List<Long> categoryIds = categoryRepository.findAllChildCategoryIds(categoryId);

		Page<BookContributorsResponse> bookContributorsResponsePage = bookCategoryRepository.findBooksByCategoryIdWithPaging(
			categoryIds,
			pageable
		);

		if (pageable.getPageNumber() > bookContributorsResponsePage.getTotalPages() - 1) {
			throw new InvalidPageRequestException("조회 가능한 페이지 범위를 초과했습니다.");
		}

		return new PagedResponse<>(
			bookContributorsResponsePage.getContent(),
			bookContributorsResponsePage.getNumber(),
			bookContributorsResponsePage.getSize(),
			bookContributorsResponsePage.getTotalPages(),
			bookContributorsResponsePage.getTotalElements()
		);

		/*List<AdminBookListResponse> adminBookListResponseList = bookCategoryRepository.findBooksByCategoryId(
			categoryIds,
			pageable);

		int totalElements = (int) bookCategoryRepository.countBookByCategoryIds(categoryIds);

		int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

		return new PagedResponse<>(
			adminBookListResponseList,
			pageable.getPageNumber(),
			pageable.getPageSize(),
			totalPages,
			totalElements
		);*/

		//int total = (int)bookCategoryRepository.countBookByCategoryIds(categoryIds);

		//return (PagedResponse<AdminBookListResponse>)PagedResponse.of(adminBookListResponseList, pageable, total);
	}

}
