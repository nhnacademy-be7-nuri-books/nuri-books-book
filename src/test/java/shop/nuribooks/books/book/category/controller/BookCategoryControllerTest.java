package shop.nuribooks.books.book.category.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import shop.nuribooks.books.book.book.dto.AdminBookListResponse;
import shop.nuribooks.books.book.book.dto.BookBriefResponse;
import shop.nuribooks.books.book.category.dto.SimpleCategoryResponse;
import shop.nuribooks.books.book.category.service.BookCategoryService;
import shop.nuribooks.books.common.message.PagedResponse;
import shop.nuribooks.books.exception.book.BookNotFoundException;
import shop.nuribooks.books.exception.category.BookCategoryAlreadyExistsException;
import shop.nuribooks.books.exception.category.BookCategoryNotFoundException;
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

	@DisplayName("이미 존재하는 연관 관계로 요청 시 BookCategoryAlreadyExistsException 발생")
	@Test
	@Order(2)
	void registerBookCategory_AlreadyExistsException() throws Exception {
		// Given
		Long bookId = 1L;
		Long categoryId = 1L;

		doThrow(new BookCategoryAlreadyExistsException(bookId, categoryId))
			.when(bookCategoryService).registerBookCategory(bookId, categoryId);

		// When & Then
		mockMvc.perform(post("/api/book-category/{bookId}/categories/{categoryId}", bookId, categoryId))
			.andExpect(status().isConflict());

		verify(bookCategoryService, times(1)).registerBookCategory(bookId, categoryId);
	}

	@DisplayName("존재하지 않는 도서 ID로 요청 시 BookNotFoundException 발생")
	@Test
	@Order(3)
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
	@Order(4)
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

	@DisplayName("도서와 카테고리가 존재하고 연관 관계가 있을 때 삭제 성공")
	@Test
	@Order(6)
	void deleteBookCategory_Success() throws Exception {
		// Given
		Long bookId = 1L;
		Long categoryId = 1L;

		// When & Then
		mockMvc.perform(delete("/api/book-category/{bookId}/categories/{categoryId}", bookId, categoryId))
			.andExpect(status().isNoContent());

		verify(bookCategoryService, times(1)).deleteBookCategory(bookId, categoryId);
	}

	@DisplayName("존재하지 않는 도서 ID로 삭제 요청 시 BookNotFoundException 발생")
	@Test
	@Order(7)
	void deleteBookCategory_BookNotFoundException() throws Exception {
		// Given
		Long bookId = 1L;
		Long categoryId = 1L;

		doThrow(new BookNotFoundException(bookId))
			.when(bookCategoryService).deleteBookCategory(bookId, categoryId);

		// When & Then
		mockMvc.perform(delete("/api/book-category/{bookId}/categories/{categoryId}", bookId, categoryId))
			.andExpect(status().isNotFound());

		verify(bookCategoryService, times(1)).deleteBookCategory(bookId, categoryId);
	}

	@DisplayName("존재하지 않는 카테고리 ID로 삭제 요청 시 CategoryNotFoundException 발생")
	@Test
	@Order(8)
	void deleteBookCategory_CategoryNotFoundException() throws Exception {
		// Given
		Long bookId = 1L;
		Long categoryId = 1L;

		doThrow(new CategoryNotFoundException())
			.when(bookCategoryService).deleteBookCategory(bookId, categoryId);

		// When & Then
		mockMvc.perform(delete("/api/book-category/{bookId}/categories/{categoryId}", bookId, categoryId))
			.andExpect(status().isNotFound());

		verify(bookCategoryService, times(1)).deleteBookCategory(bookId, categoryId);
	}

	@DisplayName("연관 관계가 존재하지 않을 때 BookCategoryNotFoundException 발생")
	@Test
	@Order(9)
	void deleteBookCategory_BookCategoryNotFoundException() throws Exception {
		// Given
		Long bookId = 1L;
		Long categoryId = 1L;

		doThrow(new BookCategoryNotFoundException(bookId, categoryId))
			.when(bookCategoryService).deleteBookCategory(bookId, categoryId);

		// When & Then
		mockMvc.perform(delete("/api/book-category/{bookId}/categories/{categoryId}", bookId, categoryId))
			.andExpect(status().isNotFound());

		verify(bookCategoryService, times(1)).deleteBookCategory(bookId, categoryId);
	}

	@DisplayName("주어진 도서 ID에 해당하는 카테고리 목록 조회 성공")
	@Test
	@Order(10)
	void getCategoriesByBookId_Success() throws Exception {
		// Given
		Long bookId = 1L;
		List<List<SimpleCategoryResponse>> categories = List.of(
			List.of(new SimpleCategoryResponse(1L, "Category1")),
			List.of(new SimpleCategoryResponse(2L, "Category2"))
		);

		when(bookCategoryService.findCategoriesByBookId(bookId)).thenReturn(categories);

		// When & Then
		mockMvc.perform(get("/api/book-category/book/{bookId}", bookId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0][0].id").value(1L))
			.andExpect(jsonPath("$[0][0].name").value("Category1"))
			.andExpect(jsonPath("$[1][0].id").value(2L))
			.andExpect(jsonPath("$[1][0].name").value("Category2"));

		verify(bookCategoryService, times(1)).findCategoriesByBookId(bookId);
	}

	@DisplayName("존재하지 않는 도서 ID로 카테고리 조회 시 BookNotFoundException 발생")
	@Test
	@Order(11)
	void getCategoriesByBookId_BookNotFoundException() throws Exception {
		// Given
		Long bookId = 1L;

		doThrow(new BookNotFoundException(bookId))
			.when(bookCategoryService).findCategoriesByBookId(bookId);

		// When & Then
		mockMvc.perform(get("/api/book-category/book/{bookId}", bookId))
			.andExpect(status().isNotFound());

		verify(bookCategoryService, times(1)).findCategoriesByBookId(bookId);
	}

	@DisplayName("카테고리 ID로 책 조회 성공")
	@Test
	@Order(12)
	void getBooksByCategoryId_Success() throws Exception {
		// Given
		Long categoryId = 1L;
		Pageable pageable = PageRequest.of(0, 10);
		List<BookBriefResponse> bookList = List.of(new BookBriefResponse(1L, "Book1", "afew"),
			new BookBriefResponse(2L, "Book2", "fewafaw"));
		PagedResponse<AdminBookListResponse> pagedResponse = PagedResponse.of(bookList, pageable, bookList.size());

		when(bookCategoryService.findBooksByCategoryId(categoryId, pageable)).thenReturn(pagedResponse);

		// When & Then
		mockMvc.perform(get("/api/book-category/category/{categoryId}", categoryId)
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].id").value(1L))
			.andExpect(jsonPath("$.content[0].title").value("Book1"))
			.andExpect(jsonPath("$.content[1].id").value(2L))
			.andExpect(jsonPath("$.content[1].title").value("Book2"));

		verify(bookCategoryService, times(1)).findBooksByCategoryId(categoryId, pageable);
	}

	@DisplayName("존재하지 않는 카테고리 ID로 책 조회 시 CategoryNotFoundException 발생")
	@Test
	@Order(13)
	void getBooksByCategoryId_CategoryNotFoundException() throws Exception {
		// Given
		Long categoryId = 1L;
		Pageable pageable = PageRequest.of(0, 10);

		doThrow(new CategoryNotFoundException()).when(bookCategoryService).findBooksByCategoryId(categoryId, pageable);

		// When & Then
		mockMvc.perform(get("/api/book-category/category/{categoryId}", categoryId)
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isNotFound());

		verify(bookCategoryService, times(1)).findBooksByCategoryId(categoryId, pageable);
	}
}
