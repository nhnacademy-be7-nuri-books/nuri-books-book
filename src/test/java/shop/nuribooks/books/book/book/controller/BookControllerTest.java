package shop.nuribooks.books.book.book.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.book.book.dto.AdminBookListResponse;
import shop.nuribooks.books.book.book.dto.BookRegisterRequest;
import shop.nuribooks.books.book.book.dto.BookRegisterResponse;
import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.dto.BookUpdateRequest;
import shop.nuribooks.books.book.book.entitiy.BookStateEnum;
import shop.nuribooks.books.book.book.service.BookService;
import shop.nuribooks.books.exception.BadRequestException;
import shop.nuribooks.books.exception.InvalidPageRequestException;
import shop.nuribooks.books.exception.ResourceNotFoundException;

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
			1L,
			BookStateEnum.NORMAL,
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
			"출판사",
			BookStateEnum.NORMAL.getKorName(),
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
			.andExpect(jsonPath("$.message", containsString("내용은 필수입니다.")));
	}

	@Test
	public void getBooks_ShouldReturnPageOfBooks_WhenRequestIsValid() throws Exception {
		Pageable pageable = PageRequest.of(0, 10);
		AdminBookListResponse bookResponse = new AdminBookListResponse(
			1L,
			null,
			BookStateEnum.NORMAL.getKorName(),
			"책 제목",
			BigDecimal.valueOf(10000),
			0,
			true,
			10
		);

		Page<AdminBookListResponse> bookPage = new PageImpl<>(List.of(bookResponse), pageable, 1);
		when(bookService.getBooks(pageable)).thenReturn(bookPage);

		mockMvc.perform(get("/api/books")
				.contentType(MediaType.APPLICATION_JSON)
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].id").value(1L))
			.andExpect(jsonPath("$.content[0].title").value("책 제목"));
	}

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

	/*@Test
	public void getBookById_ShouldReturnBookResponse_WhenBookExists() throws Exception {
		Long bookId = 1L;
		BookResponse bookResponse = new BookResponse(
			bookId, null, "정상", "책 제목", "thumbnail.jpg", null, LocalDate.now(),
			BigDecimal.valueOf(10000), 10, "책 설명", "책 내용", "1234567890123",
			true, 0, 10, 100L);

		when(bookService.getBookById(bookId)).thenReturn(bookResponse);

		mockMvc.perform(get("/api/books/{bookId}", bookId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(bookId))
			.andExpect(jsonPath("$.title").value("책 제목"));
	}*/

	/*@Test
	public void getBookById_ShouldReturnNotFound_WhenBookDoesNotExist() throws Exception {
		Long bookId = 9999L;

		when(bookService.getBookById(bookId)).thenThrow(new ResourceNotFoundException("도서를 찾을 수 없습니다."));

		mockMvc.perform(get("/api/books/{bookId}", bookId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("도서를 찾을 수 없습니다."));
	}*/

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
