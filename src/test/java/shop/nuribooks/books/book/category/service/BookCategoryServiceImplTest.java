package shop.nuribooks.books.book.category.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

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

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
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
public class BookCategoryServiceImplTest {

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

}
