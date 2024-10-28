package shop.nuribooks.books.book.category.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.category.entitiy.BookCategory;
import shop.nuribooks.books.book.category.entitiy.Category;
import shop.nuribooks.books.book.category.repository.BookCategoryRepository;
import shop.nuribooks.books.book.category.repository.CategoryRepository;
import shop.nuribooks.books.book.category.service.BookCategoryService;
import shop.nuribooks.books.exception.book.BookNotFoundException;
import shop.nuribooks.books.exception.category.CategoryNotFoundException;

/**
 * 도서와 카테고리 간의 연관 관계를 관리하는 서비스 구현 클래스입니다.
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
	 * @author janghyun
	 * @param bookId     연관시킬 도서의 ID
	 * @param categoryId 연관시킬 카테고리의 ID
	 * @throws BookNotFoundException     해당 ID의 도서를 찾을 수 없을 때 발생합니다.
	 * @throws CategoryNotFoundException 해당 ID의 카테고리를 찾을 수 없을 때 발생합니다.
	 */
	@Override
	public void registerBookCategory(Long bookId, Long categoryId) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new BookNotFoundException(bookId));

		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(CategoryNotFoundException::new);

		bookCategoryRepository.save(BookCategory.builder()
			.book(book)
			.category(category)
			.build());
	}

}
