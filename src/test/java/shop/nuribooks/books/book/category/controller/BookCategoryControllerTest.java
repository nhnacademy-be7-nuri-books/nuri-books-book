package shop.nuribooks.books.book.category.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import shop.nuribooks.books.book.category.service.BookCategoryService;
import shop.nuribooks.books.exception.book.BookNotFoundException;
import shop.nuribooks.books.exception.category.CategoryNotFoundException;

@WebMvcTest(BookCategoryController.class)
@TestMethodOrder(OrderAnnotation.class)
public class BookCategoryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookCategoryService bookCategoryService;

	@DisplayName("도서와 카테고리가 존재할 때 연관 관계 생성 성공")
	@Test
	@Order(1)
	void registerBookCategory_Success() throws Exception {
		// Given
		Long bookId = 1L;
		Long categoryId = 1L;

		// When & Then
		mockMvc.perform(post("/api/book-category/{bookId}/categories/{categoryId}", bookId, categoryId))
			.andExpect(status().isCreated());

		verify(bookCategoryService, times(1)).registerBookCategory(bookId, categoryId);
	}

	@DisplayName("존재하지 않는 도서 ID로 요청 시 BookNotFoundException 발생")
	@Test
	@Order(2)
	void registerBookCategory_BookNotFoundException() throws Exception {
		// Given
		Long bookId = 1L;
		Long categoryId = 1L;

		doThrow(new BookNotFoundException(bookId))
			.when(bookCategoryService).registerBookCategory(bookId, categoryId);

		// When & Then
		mockMvc.perform(post("/api/book-category/{bookId}/categories/{categoryId}", bookId, categoryId))
			.andExpect(status().isNotFound());

		verify(bookCategoryService, times(1)).registerBookCategory(bookId, categoryId);
	}

	@DisplayName("존재하지 않는 카테고리 ID로 요청 시 CategoryNotFoundException 발생")
	@Test
	@Order(3)
	void registerBookCategory_CategoryNotFoundException() throws Exception {
		// Given
		Long bookId = 1L;
		Long categoryId = 1L;

		doThrow(new CategoryNotFoundException())
			.when(bookCategoryService).registerBookCategory(bookId, categoryId);

		// When & Then
		mockMvc.perform(post("/api/book-category/{bookId}/categories/{categoryId}", bookId, categoryId))
			.andExpect(status().isNotFound());

		verify(bookCategoryService, times(1)).registerBookCategory(bookId, categoryId);
	}

	@DisplayName("서버 내부 오류 발생 시 500 에러 반환")
	@Test
	@Order(4)
	void registerBookCategory_InternalServerError() throws Exception {
		// Given
		Long bookId = 1L;
		Long categoryId = 1L;

		doThrow(new RuntimeException("Internal Server Error"))
			.when(bookCategoryService).registerBookCategory(bookId, categoryId);

		// When & Then
		mockMvc.perform(post("/api/book-category/{bookId}/categories/{categoryId}", bookId, categoryId))
			.andExpect(status().isInternalServerError());

		verify(bookCategoryService, times(1)).registerBookCategory(bookId, categoryId);
	}
}
