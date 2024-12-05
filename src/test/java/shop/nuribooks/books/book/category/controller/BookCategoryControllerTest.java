package shop.nuribooks.books.book.category.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.book.book.dto.response.BookContributorsResponse;
import shop.nuribooks.books.book.category.dto.SimpleCategoryResponse;
import shop.nuribooks.books.book.category.service.BookCategoryService;
import shop.nuribooks.books.exception.book.BookNotFoundException;
import shop.nuribooks.books.exception.category.CategoryNotFoundException;

@WebMvcTest(BookCategoryController.class)
class BookCategoryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookCategoryService bookCategoryService;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		mockMvc = mockMvc;
	}

	@DisplayName("도서와 카테고리 연관 성공")
	@Test
	void registerBookCategory_Success() throws Exception {
		// Given
		Long bookId = 1L;
		Long categoryId = 2L;

		doNothing().when(bookCategoryService).registerBookCategory(bookId, categoryId);

		// When & Then
		mockMvc.perform(post("/api/books/{bookId}/categories/{categoryId}", bookId, categoryId))
			.andExpect(status().isCreated());

		verify(bookCategoryService, times(1)).registerBookCategory(bookId, categoryId);
	}

	@DisplayName("도서와 카테고리 연관 삭제 성공")
	@Test
	void deleteBookCategory_Success() throws Exception {
		// Given
		Long bookId = 1L;
		Long categoryId = 2L;

		doNothing().when(bookCategoryService).deleteBookCategory(bookId, categoryId);

		// When & Then
		mockMvc.perform(delete("/api/books/{bookId}/categories/{categoryId}", bookId, categoryId))
			.andExpect(status().isNoContent());

		verify(bookCategoryService, times(1)).deleteBookCategory(bookId, categoryId);
	}

	@DisplayName("도서 ID로 카테고리 조회 성공")
	@Test
	void getCategoriesByBookId_Success() throws Exception {
		// Given
		Long bookId = 1L;
		List<List<SimpleCategoryResponse>> categories = List.of(
			List.of(new SimpleCategoryResponse(1L, "Category1")),
			List.of(new SimpleCategoryResponse(2L, "Category2"))
		);

		when(bookCategoryService.findCategoriesByBookId(bookId)).thenReturn(categories);

		// When & Then
		mockMvc.perform(get("/api/books/book/{bookId}", bookId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(2))
			.andExpect(jsonPath("$[0][0].name").value("Category1"))
			.andExpect(jsonPath("$[1][0].name").value("Category2"));

		verify(bookCategoryService, times(1)).findCategoriesByBookId(bookId);
	}

	@DisplayName("카테고리 ID로 책 조회 성공")
	@Test
	void getBooksByCategoryId_Success() throws Exception {
		// Given
		Long categoryId = 1L;
		Pageable pageable = PageRequest.of(0, 10);
		List<BookContributorsResponse> bookList = List.of(
			new BookContributorsResponse(null, null)
		);
		Page<BookContributorsResponse> pagedResponse = new PageImpl<>(bookList, pageable, 1);

		when(bookCategoryService.findBooksByCategoryId(categoryId, pageable)).thenReturn(pagedResponse);

		// When & Then
		mockMvc.perform(get("/api/books/category/{category-id}", categoryId)
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content.length()").value(1));

		verify(bookCategoryService, times(1)).findBooksByCategoryId(categoryId, pageable);
	}

	@DisplayName("도서 ID가 없을 때 BookNotFoundException 발생")
	@Test
	void getCategoriesByBookId_BookNotFoundException() throws Exception {
		// Given
		Long bookId = 999L;

		when(bookCategoryService.findCategoriesByBookId(bookId)).thenThrow(
			new BookNotFoundException(bookId));

		// When & Then
		mockMvc.perform(get("/api/books/book/{bookId}", bookId))
			.andExpect(status().isNotFound());

		verify(bookCategoryService, times(1)).findCategoriesByBookId(bookId);
	}

	@DisplayName("카테고리 ID가 없을 때 CategoryNotFoundException 발생")
	@Test
	void getBooksByCategoryId_CategoryNotFoundException() throws Exception {
		// Given
		Long categoryId = 999L;
		Pageable pageable = PageRequest.of(0, 10);

		when(bookCategoryService.findBooksByCategoryId(categoryId, pageable))
			.thenThrow(new CategoryNotFoundException());

		// When & Then
		mockMvc.perform(get("/api/books/category/{category-id}", categoryId)
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isNotFound());

		verify(bookCategoryService, times(1)).findBooksByCategoryId(categoryId, pageable);
	}
}
