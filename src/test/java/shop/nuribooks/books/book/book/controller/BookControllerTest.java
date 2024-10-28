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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.book.book.dto.BookRegisterRequest;
import shop.nuribooks.books.book.book.dto.BookRegisterResponse;
import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.dto.BookUpdateRequest;
import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.bookstate.entitiy.BookState;
import shop.nuribooks.books.book.publisher.entitiy.Publisher;
import shop.nuribooks.books.exception.BadRequestException;
import shop.nuribooks.books.exception.InvalidPageRequestException;
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
	public void getBooks_ShouldReturnPageOfBooks_WhenRequestIsValid() throws Exception {
		BookState bookState = BookState.builder().detail("InStock").build();
		Publisher publisher = new Publisher(1L, "Publisher Name");
		Pageable pageable = PageRequest.of(0, 10);

		Book book = Book.builder()
			.stateId(bookState)
			.publisherId(publisher)
			.title("책 제목")
			.thumbnailImageUrl("thumbnail.jpg")
			.detailImageUrl("detail.jpg")
			.publicationDate(LocalDate.now())
			.price(BigDecimal.valueOf(10000))
			.discountRate(0)
			.description("책 설명")
			.contents("책 내용")
			.isbn("1234567890123")
			.isPackageable(true)
			.stock(10)
			.likeCount(10)
			.viewCount(50L)
			.build();

		BookResponse bookResponse = BookResponse.of(book);
		Page<BookResponse> bookPage = new PageImpl<>(List.of(bookResponse), pageable, 1);
		when(bookService.getBooks(pageable)).thenReturn(bookPage);

		mockMvc.perform(get("/api/books")
				.contentType(MediaType.APPLICATION_JSON)
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].id").value(book.getId()))
			.andExpect(jsonPath("$.content[0].title").value("책 제목"));
	}

	@Test
	public void getBooks_ShouldReturnBadRequest_WhenPageIsOutOfRange() throws Exception {
		Pageable pageable = PageRequest.of(10, 10);
		when(bookService.getBooks(pageable)).thenThrow(new InvalidPageRequestException());

		mockMvc.perform(get("/api/books")
				.contentType(MediaType.APPLICATION_JSON)
				.param("page", "10")
				.param("size", "10"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("페이지 범위를 초과했습니다."));
	}

	@Test
	public void getBookById_ShouldReturnBook_WhenBookExists() throws Exception {
		Long bookId = 1L;
		BookState bookState = BookState.builder().detail("InStock").build();
		Publisher publisher = new Publisher(1L, "Publisher Name");

		Book book = Book.builder()
			.stateId(bookState)
			.publisherId(publisher)
			.title("책 제목")
			.thumbnailImageUrl("thumbnail.jpg")
			.detailImageUrl("detail.jpg")
			.publicationDate(LocalDate.now())
			.price(BigDecimal.valueOf(10000))
			.discountRate(0)
			.description("책 설명")
			.contents("책 내용")
			.isbn("1234567890123")
			.isPackageable(true)
			.stock(10)
			.likeCount(10)
			.viewCount(50L)
			.build();

		ReflectionTestUtils.setField(book, "id", bookId);

		BookResponse bookResponse = BookResponse.of(book);
		when(bookService.getBookById(bookId)).thenReturn(bookResponse);

		mockMvc.perform(get("/api/books/{bookId}", bookId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(bookId))
			.andExpect(jsonPath("$.title").value("책 제목"));
	}

	@Test
	public void getBookById_ShouldReturnNotFound_WhenBookDoesNotExist() throws Exception {
		Long bookId = 9999L;
		when(bookService.getBookById(bookId)).thenThrow(new ResourceNotFoundException("도서를 찾을 수 없습니다."));

		mockMvc.perform(get("/api/books/{bookId}", bookId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("도서를 찾을 수 없습니다."));
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

	@Test
	public void deleteBook_ShouldReturnOk_WhenBookExists() throws Exception {
		Long bookId = 1L;

		doNothing().when(bookService).deleteBook(bookId);

		mockMvc.perform(delete("/api/books/{bookId}", bookId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());
			//.andExpect(jsonPath("$.message").value("도서 삭제 성공"));
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
