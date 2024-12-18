package shop.nuribooks.books.book.category.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.book.dto.response.BookContributorsResponse;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.category.dto.SimpleCategoryResponse;
import shop.nuribooks.books.book.category.entity.BookCategory;
import shop.nuribooks.books.book.category.entity.Category;
import shop.nuribooks.books.book.category.repository.BookCategoryRepository;
import shop.nuribooks.books.book.category.repository.CategoryRepository;
import shop.nuribooks.books.book.category.service.impl.BookCategoryServiceImpl;
import shop.nuribooks.books.exception.book.BookNotFoundException;
import shop.nuribooks.books.exception.category.BookCategoryAlreadyExistsException;
import shop.nuribooks.books.exception.category.BookCategoryNotFoundException;
import shop.nuribooks.books.exception.category.CategoryNotFoundException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookCategoryServiceImplTest {

	@Mock
	private BookRepository bookRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private BookCategoryRepository bookCategoryRepository;

	@InjectMocks
	private BookCategoryServiceImpl bookCategoryService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@DisplayName("책과 카테고리가 존재할 때 BookCategory 등록 성공")
	@Order(1)
	@Test
	void registerBookCategory_Success() {
		// Given
		Long bookId = 1L;
		Long categoryId = 1L;

		Book book = mock(Book.class);
		Category category = mock(Category.class);

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
		when(bookCategoryRepository.save(any(BookCategory.class))).thenReturn(null);

		// When
		bookCategoryService.registerBookCategory(bookId, categoryId);

		// Then
		verify(bookRepository, times(1)).findById(bookId);
		verify(categoryRepository, times(1)).findById(categoryId);
		verify(bookCategoryRepository, times(1)).save(any(BookCategory.class));
	}

	@DisplayName("책과 카테고리가 이미 연관되어 있을 때 BookCategoryAlreadyExistsException 발생")
	@Test
	@Order(2)
	void registerBookCategory_AlreadyExistsException() {
		// Given
		Long bookId = 1L;
		Long categoryId = 1L;

		Book book = mock(Book.class);
		Category category = mock(Category.class);

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
		when(bookCategoryRepository.existsByBookAndCategory(book, category)).thenReturn(true);

		// When & Then
		assertThatThrownBy(() -> bookCategoryService.registerBookCategory(bookId, categoryId))
			.isInstanceOf(BookCategoryAlreadyExistsException.class)
			.hasMessageContaining(String.valueOf(bookId))
			.hasMessageContaining(String.valueOf(categoryId));

		verify(bookRepository, times(1)).findById(bookId);
		verify(categoryRepository, times(1)).findById(categoryId);
		verify(bookCategoryRepository, times(1)).existsByBookAndCategory(book, category);
		verify(bookCategoryRepository, never()).save(any(BookCategory.class));
	}

	@DisplayName("책이 존재하지 않을 때 BookNotFoundException 발생")
	@Order(3)
	@Test
	void registerBookCategory_BookNotFoundException() {
		// Given
		Long bookId = 1L;
		Long categoryId = 1L;

		when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> bookCategoryService.registerBookCategory(bookId, categoryId))
			.isInstanceOf(BookNotFoundException.class)
			.hasMessageContaining(String.valueOf(bookId));

		verify(bookRepository, times(1)).findById(bookId);
		verify(categoryRepository, never()).findById(anyLong());
		verify(bookCategoryRepository, never()).save(any(BookCategory.class));
	}

	@DisplayName("카테고리가 존재하지 않을 때 CategoryNotFoundException 발생")
	@Order(4)
	@Test
	void registerBookCategory_CategoryNotFoundException() {
		// Given
		Long bookId = 1L;
		Long categoryId = 1L;

		Book book = mock(Book.class);

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> bookCategoryService.registerBookCategory(bookId, categoryId))
			.isInstanceOf(CategoryNotFoundException.class);

		verify(bookRepository, times(1)).findById(bookId);
		verify(categoryRepository, times(1)).findById(categoryId);
		verify(bookCategoryRepository, never()).save(any(BookCategory.class));
	}

	@DisplayName("도서와 카테고리가 존재하고 연관 관계가 있을 때 삭제 성공")
	@Test
	@Order(5)
	void deleteBookCategory_Success() {
		// Given
		Long bookId = 1L;
		Long categoryId = 1L;

		Book book = mock(Book.class);
		Category category = mock(Category.class);
		BookCategory bookCategory = mock(BookCategory.class);

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
		when(bookCategoryRepository.findByBookAndCategory(book, category)).thenReturn(Optional.of(bookCategory));

		// When
		bookCategoryService.deleteBookCategory(bookId, categoryId);

		// Then
		verify(bookRepository, times(1)).findById(bookId);
		verify(categoryRepository, times(1)).findById(categoryId);
		verify(bookCategoryRepository, times(1)).findByBookAndCategory(book, category);
		verify(bookCategoryRepository, times(1)).delete(bookCategory);
	}

	@DisplayName("삭제하려는 도서가 존재하지 않을 때 BookNotFoundException 발생")
	@Test
	@Order(6)
	void deleteBookCategory_BookNotFoundException() {
		// Given
		Long bookId = 1L;
		Long categoryId = 1L;

		when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> bookCategoryService.deleteBookCategory(bookId, categoryId))
			.isInstanceOf(BookNotFoundException.class)
			.hasMessageContaining(String.valueOf(bookId));

		verify(bookRepository, times(1)).findById(bookId);
		verify(categoryRepository, never()).findById(anyLong());
		verify(bookCategoryRepository, never()).findByBookAndCategory(any(), any());
		verify(bookCategoryRepository, never()).delete(any());
	}

	@DisplayName("삭제하려는 카테고리가 존재하지 않을 때 CategoryNotFoundException 발생")
	@Test
	@Order(7)
	void deleteBookCategory_CategoryNotFoundException() {
		// Given
		Long bookId = 1L;
		Long categoryId = 1L;

		Book book = mock(Book.class);

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> bookCategoryService.deleteBookCategory(bookId, categoryId))
			.isInstanceOf(CategoryNotFoundException.class);

		verify(bookRepository, times(1)).findById(bookId);
		verify(categoryRepository, times(1)).findById(categoryId);
		verify(bookCategoryRepository, never()).findByBookAndCategory(any(), any());
		verify(bookCategoryRepository, never()).delete(any());
	}

	@DisplayName("연관 관계가 존재하지 않을 때 BookCategoryNotFoundException 발생")
	@Test
	@Order(8)
	void deleteBookCategory_BookCategoryNotFoundException() {
		// Given
		Long bookId = 1L;
		Long categoryId = 1L;

		Book book = mock(Book.class);
		Category category = mock(Category.class);

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
		when(bookCategoryRepository.findByBookAndCategory(book, category)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> bookCategoryService.deleteBookCategory(bookId, categoryId))
			.isInstanceOf(BookCategoryNotFoundException.class)
			.hasMessageContaining("북카테고리를 찾을 수 없습니다.")
			.hasMessageContaining(String.valueOf(bookId))
			.hasMessageContaining(String.valueOf(categoryId));

		verify(bookRepository, times(1)).findById(bookId);
		verify(categoryRepository, times(1)).findById(categoryId);
		verify(bookCategoryRepository, times(1)).findByBookAndCategory(book, category);
		verify(bookCategoryRepository, never()).delete(any());
	}

	@DisplayName("주어진 도서 ID에 해당하는 카테고리 목록 조회 성공")
	@Test
	@Order(9)
	void findCategoriesByBookId_Success() {
		// Given
		Long bookId = 1L;
		List<List<SimpleCategoryResponse>> categories = List.of(
			List.of(new SimpleCategoryResponse(1L, "Category1")),
			List.of(new SimpleCategoryResponse(2L, "Category2"))
		);

		when(bookRepository.existsById(bookId)).thenReturn(true);
		when(bookCategoryRepository.findCategoriesByBookId(bookId)).thenReturn(categories);

		// When
		List<List<SimpleCategoryResponse>> result = bookCategoryService.findCategoriesByBookId(bookId);

		// Then
		assertThat(result)
			.isNotNull()
			.hasSize(2);
		assertThat(result.get(0)).extracting(SimpleCategoryResponse::name)
			.containsExactly("Category1");
		assertThat(result.get(1)).extracting(SimpleCategoryResponse::name)
			.containsExactly("Category2");

		verify(bookRepository, times(1)).existsById(bookId);
		verify(bookCategoryRepository, times(1)).findCategoriesByBookId(bookId);
	}

	@DisplayName("주어진 도서 ID가 존재하지 않을 때 BookNotFoundException 발생")
	@Test
	@Order(10)
	void findCategoriesByBookId_BookNotFoundException() {
		// Given
		Long bookId = 1L;

		when(bookRepository.existsById(bookId)).thenReturn(false);

		// When & Then
		assertThatThrownBy(() -> bookCategoryService.findCategoriesByBookId(bookId))
			.isInstanceOf(BookNotFoundException.class)
			.hasMessageContaining(String.valueOf(bookId));

		verify(bookRepository, times(1)).existsById(bookId);
		verify(bookCategoryRepository, never()).findCategoriesByBookId(bookId);
	}

	@DisplayName("존재하지 않는 카테고리 ID로 조회 시 CategoryNotFoundException 발생")
	@Test
	@Order(12)
	void findBooksByCategoryId_CategoryNotFoundException() {
		// Given
		Long categoryId = 1L;
		Pageable pageable = PageRequest.of(0, 10);

		when(categoryRepository.existsById(categoryId)).thenReturn(false);

		// When & Then
		assertThatThrownBy(() -> bookCategoryService.findBooksByCategoryId(categoryId, pageable))
			.isInstanceOf(CategoryNotFoundException.class);

		verify(categoryRepository, times(1)).existsById(categoryId);
		verify(categoryRepository, never()).findAllChildCategoryIds(anyLong());
		verify(bookCategoryRepository, never()).findBooksByCategoryId(anyList(), any());
		verify(bookCategoryRepository, never()).countBookByCategoryIds(anyList());
	}

	@DisplayName("도서 ID로 모든 연관된 카테고리 삭제 성공")
	@Test
	@Order(11)
	void deleteBookCategories_Success() {
		// Given
		Long bookId = 1L;
		List<BookCategory> bookCategories = List.of(
			mock(BookCategory.class),
			mock(BookCategory.class)
		);

		when(bookCategoryRepository.findByBookId(bookId)).thenReturn(bookCategories);

		// When
		bookCategoryService.deleteBookCategories(bookId);

		// Then
		verify(bookCategoryRepository, times(1)).findByBookId(bookId);
		verify(bookCategoryRepository, times(1)).deleteAll(bookCategories);
	}

	@DisplayName("카테고리 ID로 책 조회 성공")
	@Test
	@Order(13)
	void findBooksByCategoryId_Success() {
		// Given
		Long categoryId = 1L;
		Pageable pageable = PageRequest.of(0, 10);
		List<Long> categoryIds = List.of(1L, 2L);
		Page<BookContributorsResponse> bookPage = mock(Page.class);

		when(categoryRepository.existsById(categoryId)).thenReturn(true);
		when(categoryRepository.findAllChildCategoryIds(categoryId)).thenReturn(categoryIds);
		when(bookCategoryRepository.findBooksByCategoryIdWithPaging(categoryIds, pageable))
			.thenReturn(bookPage);

		// When
		Page<BookContributorsResponse> result = bookCategoryService.findBooksByCategoryId(categoryId, pageable);

		// Then
		assertThat(result).isNotNull();
		verify(categoryRepository, times(1)).existsById(categoryId);
		verify(categoryRepository, times(1)).findAllChildCategoryIds(categoryId);
		verify(bookCategoryRepository, times(1)).findBooksByCategoryIdWithPaging(categoryIds, pageable);
	}
}
