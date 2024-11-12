package shop.nuribooks.books.book.book.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.book.book.dto.BookRegisterRequest;
import shop.nuribooks.books.book.book.dto.BookRegisterResponse;
import shop.nuribooks.books.book.book.dto.BookUpdateRequest;
import shop.nuribooks.books.book.book.entity.BookStateEnum;
import shop.nuribooks.books.book.book.service.BookService;
import shop.nuribooks.books.common.ControllerTestSupport;
import shop.nuribooks.books.exception.BadRequestException;
import shop.nuribooks.books.exception.InvalidPageRequestException;
import shop.nuribooks.books.exception.ResourceNotFoundException;

public class BookControllerTest extends ControllerTestSupport {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void getBooks_ShouldReturnBadRequest_WhenPageIsOutOfRange() throws Exception {
		Pageable pageable = PageRequest.of(10, 10);
		when(bookService.getBooks(pageable)).thenThrow(new InvalidPageRequestException("조회 가능한 페이지 범위를 초과했습니다."));

		mockMvc.perform(get("/api/books")
				.contentType(MediaType.APPLICATION_JSON)
				.param("page", "10")
				.param("size", "10"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("조회 가능한 페이지 범위를 초과했습니다."));
	}

	@Test
	public void updateBook_ShouldReturnOk_WhenRequestIsValid() throws Exception {
		Long bookId = 1L;
		BookUpdateRequest bookUpdateReq = new BookUpdateRequest(
			1L,
			BookStateEnum.NEW,
			"수정된 제목",
			"updated_thumbnail.jpg",
			"updated_detail.jpg",
			LocalDate.parse("2024-10-22"),
			BigDecimal.valueOf(20000),
			10,
			"수정된 설명",
			"수정된 내용",
			"0987654321098",
			true,
			20
		);

		doNothing().when(bookService).updateBook(bookId, bookUpdateReq);

		mockMvc.perform(put("/api/books/{bookId}", bookId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(bookUpdateReq)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("도서 수정 성공"));
	}

	@Test
	public void deleteBook_ShouldReturnNoContent_WhenBookExists() throws Exception {
		Long bookId = 1L;

		doNothing().when(bookService).deleteBook(bookId);

		mockMvc.perform(delete("/api/books/{bookId}", bookId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());
	}

	@Test
	public void deleteBook_ShouldReturnNotFound_WhenBookDoesNotExist() throws Exception {
		Long bookId = 9999L;

		doThrow(new ResourceNotFoundException("도서를 찾을 수 없습니다.")).when(bookService).deleteBook(bookId);

		mockMvc.perform(delete("/api/books/{bookId}", bookId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("도서를 찾을 수 없습니다."));
	}
}
