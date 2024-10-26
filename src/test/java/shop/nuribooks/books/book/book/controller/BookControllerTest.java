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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.book.book.dto.BookRegisterRequest;
import shop.nuribooks.books.book.book.dto.BookRegisterResponse;
import shop.nuribooks.books.book.book.dto.BookUpdateRequest;
import shop.nuribooks.books.common.message.ResponseMessage;
import shop.nuribooks.books.exception.BadRequestException;
import shop.nuribooks.books.exception.ResourceNotFoundException;
import shop.nuribooks.books.book.book.service.BookService;

@WebMvcTest(BookController.class)
public class BookControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookService bookService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void registerBooks_ShouldReturnCreated_WhenRequestIsValid() throws Exception {
		BookRegisterRequest reqDto = new BookRegisterRequest(
			1,
			1L,
			"책 제목",
			"thumbnail.jpg",
			"detail.jpg",
			LocalDate.parse("2024-10-21"),
			BigDecimal.valueOf(10000),
			0,
			"책 설명",
			"책 내용",
			"1234567890123",
			true,
			10
		);

		BookRegisterResponse resDto = new BookRegisterResponse(
			1L,
			1,
			1L,
			"책 제목",
			"thumbnail.jpg",
			LocalDate.parse("2024-10-21"),
			BigDecimal.valueOf(10000),
			0,
			"책 설명",
			10
		);

		when(bookService.registerBook(any(BookRegisterRequest.class))).thenReturn(resDto);

		mockMvc.perform(post("/api/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reqDto)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").value(1L))
			.andExpect(jsonPath("$.title").value("책 제목"));
	}

	@Test
	public void registerBooks_ShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {
		BookRegisterRequest reqDto = new BookRegisterRequest(
			null,
			null,
			"",
			null,
			null,
			null,
			null,
			-1,
			null,
			null,
			null,
			false,
			-1
		);

		when(bookService.registerBook(any(BookRegisterRequest.class)))
			.thenThrow(new BadRequestException("잘못된 요청 데이터입니다."));

		mockMvc.perform(post("/api/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reqDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message", containsString("설명은 필수입니다.")))
			.andExpect(jsonPath("$.message", containsString("내용은 필수입니다.")))
			.andExpect(jsonPath("$.message", containsString("도서 상태 ID는 필수입니다.")))
			.andExpect(jsonPath("$.message", containsString("제목은 1자 이상 50자 이하여야 합니다.")))
			.andExpect(jsonPath("$.message", containsString("출판사 ID는 필수입니다.")))
			.andExpect(jsonPath("$.message", containsString("할인율은 0 이상이어야 합니다.")))
			.andExpect(jsonPath("$.message", containsString("ISBN은 필수입니다.")))
			.andExpect(jsonPath("$.message", containsString("출판 날짜는 필수입니다.")))
			.andExpect(jsonPath("$.message", containsString("재고는 0 이상이어야 합니다.")))
			.andExpect(jsonPath("$.message", containsString("썸네일 이미지 URL은 필수입니다.")))
			.andExpect(jsonPath("$.message", containsString("가격은 필수입니다.")));
	}

	@Test
	public void registerBooks_ShouldReturnNotFound_WhenBookStateOrPublisherNotFound() throws Exception {
		BookRegisterRequest reqDto = new BookRegisterRequest(
			9999,
			9999L,
			"책 제목",
			"thumbnail.jpg",
			"detail.jpg",
			LocalDate.now(),
			BigDecimal.valueOf(10000),
			0,
			"책 설명",
			"책 내용",
			"1234567890123",
			true,
			10
		);

		when(bookService.registerBook(any(BookRegisterRequest.class)))
			.thenThrow(new ResourceNotFoundException("해당 id를 찾지 못했습니다."));

		mockMvc.perform(post("/api/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reqDto)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("해당 id를 찾지 못했습니다."));
	}

	@Test
	public void updateBook_ShouldReturnOk_WhenRequestIsValid() throws Exception {
		Long bookId = 1L;
		BookUpdateRequest bookUpdateReq = new BookUpdateRequest(
			1,
			1L,
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
	public void updateBook_ShouldReturnNotFound_WhenBookIdDoesNotExist() throws Exception {
		Long bookId = 9999L;
		BookUpdateRequest bookUpdateReq = new BookUpdateRequest(
			1,
			1L,
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

		doThrow(new ResourceNotFoundException("도서를 찾을 수 없습니다.")).when(bookService).updateBook(bookId, bookUpdateReq);

		mockMvc.perform(put("/api/books/{bookId}", bookId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(bookUpdateReq)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("도서를 찾을 수 없습니다."));
	}

	@Test
	public void updateBook_ShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {
		Long bookId = 1L;
		BookUpdateRequest bookUpdateReq = new BookUpdateRequest(
			null,
			null,
			"",
			null,
			null,
			null,
			null,
			-1,
			null,
			null,
			null,
			false,
			-1
		);

		doThrow(new BadRequestException("잘못된 요청 데이터입니다.")).when(bookService).updateBook(bookId, bookUpdateReq);

		mockMvc.perform(put("/api/books/{bookId}", bookId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(bookUpdateReq)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message", containsString("도서 제목은 1자에서 50자 사이여야 합니다.")))
			.andExpect(jsonPath("$.message", containsString("할인율은 0 이상이어야 합니다.")))
			.andExpect(jsonPath("$.message", containsString("도서 내용은 필수 입력 항목입니다.")))
			.andExpect(jsonPath("$.message", containsString("썸네일 이미지 URL은 필수 입력 항목입니다.")))
			.andExpect(jsonPath("$.message", containsString("출판사 ID는 필수 입력 항목입니다.")))
			.andExpect(jsonPath("$.message", containsString("도서 상태 ID는 필수 입력 항목입니다.")))
			.andExpect(jsonPath("$.message", containsString("재고는 0 이상이어야 합니다.")))
			.andExpect(jsonPath("$.message", containsString("출판일은 필수 입력 항목입니다.")))
			.andExpect(jsonPath("$.message", containsString("ISBN은 필수 입력 항목입니다.")))
			.andExpect(jsonPath("$.message", containsString("가격은 필수 입력 항목입니다.")))
			.andExpect(jsonPath("$.message", containsString("도서 설명은 필수 입력 항목입니다.")));
	}
}
