package shop.nuribooks.books.book.category.service.impl;

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

import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.category.entitiy.BookCategory;
import shop.nuribooks.books.book.category.entitiy.Category;
import shop.nuribooks.books.book.category.repository.BookCategoryRepository;
import shop.nuribooks.books.book.category.repository.CategoryRepository;
import shop.nuribooks.books.exception.book.BookNotFoundException;
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

	@DisplayName("책이 존재하지 않을 때 BookNotFoundException 발생")
	@Order(2)
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
	@Order(3)
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
}
