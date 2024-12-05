package shop.nuribooks.books.book.book.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.book.book.dto.request.AladinBookRegisterRequest;
import shop.nuribooks.books.book.book.dto.request.BookUpdateRequest;
import shop.nuribooks.books.book.book.dto.request.PersonallyBookRegisterRequest;
import shop.nuribooks.books.book.book.dto.response.BookContributorsResponse;
import shop.nuribooks.books.book.book.dto.response.BookListResponse;
import shop.nuribooks.books.book.book.dto.response.BookResponse;
import shop.nuribooks.books.book.book.dto.response.TopBookResponse;
import shop.nuribooks.books.book.book.entity.BookStateEnum;
import shop.nuribooks.books.book.book.service.BookService;
import shop.nuribooks.books.book.category.dto.SimpleCategoryResponse;

@WebMvcTest(BookController.class)
class BookControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private BookService bookService;

	@Test
	void registerAladinBook() throws Exception {
		AladinBookRegisterRequest aladinRequest = new AladinBookRegisterRequest(
			"알라딘",
			"알라딘 (작가)",
			"알라딘출판사",
			LocalDate.of(2024, 12, 5),
			BigDecimal.valueOf(15000L),
			10,
			10,
			"정상판매",
			"thumbnail.jpg",
			"본문",
			"목차",
			"K123456789",
			true,
			List.of(1L, 2L),
			"국내도서 > 국내소설"
		);

		doNothing().when(bookService).registerBook(aladinRequest);

		mockMvc.perform(post("/api/books/register/aladin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(aladinRequest)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.message").value("도서 등록 성공"));
	}

	@Test
	void registerPersonallyBook() throws Exception {
		PersonallyBookRegisterRequest personallyRequest = new PersonallyBookRegisterRequest(
			"직접",
			"직접 (작가)",
			"직접출판사",
			LocalDate.of(2024, 12, 5),
			BigDecimal.valueOf(15000L),
			10,
			10,
			"정상판매",
			"thumbnail.jpg",
			"본문",
			"목차",
			"K123456789",
			true,
			List.of(1L, 2L),
			List.of(1L, 2L)
		);

		doNothing().when(bookService).registerBook(personallyRequest);

		mockMvc.perform(post("/api/books/register/personal")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(personallyRequest)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.message").value("도서 등록 성공"));
	}

	@Test
	void getBooks() throws Exception {
		Map<String, List<String>> contributors = new HashMap<>();
		contributors.put("지은이", List.of("이", "정", "규"));

		BookListResponse bookListResponse = new BookListResponse(
			1L,
			"누리북스",
			BookStateEnum.NEW,
			"제목1",
			LocalDate.of(2024, 12, 1),
			BigDecimal.valueOf(10000L),
			10,
			"thumbnail.jpg"
		);

		BookContributorsResponse response = new BookContributorsResponse(
			bookListResponse,
			contributors
		);

		when(bookService.getBooks(any())).thenReturn(new PageImpl<>(List.of(response)));

		mockMvc.perform(get("/api/books"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.*", Matchers.hasSize(11)));

	}

	@Test
	void getBookById() throws Exception {
		Long bookId = 1L;

		Map<String, List<String>> contributorsByRole = new HashMap<>();
		contributorsByRole.put("지은이", List.of("이", "정", "규"));

		List<List<SimpleCategoryResponse>> simpleCategories = new ArrayList<>(
			List.of(
				new ArrayList<>(List.of(
					new SimpleCategoryResponse(1L, "국내도서"),
					new SimpleCategoryResponse(2L, "국내소설")
				)),
				new ArrayList<>(List.of(
					new SimpleCategoryResponse(1L, "국내도서"),
					new SimpleCategoryResponse(3L, "국내문학")
				))
			)
		);

		BookResponse bookResponse = new BookResponse(
			bookId,
			"출판사",
			"정상판매",
			"제목",
			"thumbnail.jpg",
			LocalDate.of(2024, 12, 5),
			BigDecimal.valueOf(10000L),
			10,
			BigDecimal.valueOf(9000L),
			"본문",
			"목차",
			"K123456789",
			false,
			10,
			100,
			1000L,
			List.of("wow", "good"),
			contributorsByRole,
			simpleCategories
		);

		when(bookService.getBookById(eq(bookId), eq(false))).thenReturn(bookResponse);

		mockMvc.perform(get("/api/books/{book-id}", bookId)
				.param("update-recent-view", "false"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(bookId))
			.andExpect(jsonPath("$.title").value("제목"));
	}

	@Test
	void updateBook() throws Exception {

		List<Long> tagIds = Arrays.asList(1L, 2L);
		List<Long> categoryIds = Arrays.asList(1L, 2L);

		Long bookId = 1L;

		BookUpdateRequest request = new BookUpdateRequest(
			BigDecimal.valueOf(20000),
			20,
			50,
			"정상판매",
			"newThumbnail.jpg",
			"New Description",
			"New Contents",
			true,
			tagIds,
			categoryIds
		);

		doNothing().when(bookService).updateBook(bookId, request);

		mockMvc.perform(put("/api/books/{bookId}", bookId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("도서 수정 성공"));
	}

	@Test
	void deleteBook() throws Exception {
		Long bookId = 1L;

		doNothing().when(bookService).deleteBook(bookId);

		mockMvc.perform(delete("/api/books/{bookId}", bookId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());
	}

	@Test
	void getTopBookLike() throws Exception {
		List<TopBookResponse> topBooks = List.of(
			new TopBookResponse(1L, "thumbnail1.jpg", "Title1"),
			new TopBookResponse(2L, "thumbnail2.jpg", "Title2")
		);

		when(bookService.getTopBookLikes()).thenReturn(topBooks);

		mockMvc.perform(get("/api/books/top/book-like"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", Matchers.hasSize(2)));
	}

	@Test
	void getTopBookScore() throws Exception {
		List<TopBookResponse> topBooks = List.of(
			new TopBookResponse(1L, "thumbnail1.jpg", "Title1"),
			new TopBookResponse(2L, "thumbnail2.jpg", "Title2")
		);

		when(bookService.getTopBookScores()).thenReturn(topBooks);

		mockMvc.perform(get("/api/books/top/book-score"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", Matchers.hasSize(2)));
	}

	@Test
	void getAllBooks() throws Exception {

		Map<String, List<String>> contributorsByRole = new HashMap<>();
		contributorsByRole.put("지은이", List.of("이", "정", "규"));

		List<List<SimpleCategoryResponse>> simpleCategories = new ArrayList<>(
			List.of(
				new ArrayList<>(List.of(
					new SimpleCategoryResponse(1L, "국내도서"),
					new SimpleCategoryResponse(2L, "국내소설")
				)),
				new ArrayList<>(List.of(
					new SimpleCategoryResponse(1L, "국내도서"),
					new SimpleCategoryResponse(3L, "국내문학")
				))
			)
		);

		List<BookResponse> allBooks = List.of(
			new BookResponse(
				1L,
				"출판사",
				"정상판매",
				"제목",
				"thumbnail.jpg",
				LocalDate.of(2024, 12, 5),
				BigDecimal.valueOf(10000L),
				10,
				BigDecimal.valueOf(9000L),
				"본문",
				"목차",
				"K123456789",
				false,
				10,
				100,
				1000L,
				List.of("wow", "good"),
				contributorsByRole,
				simpleCategories
			),
			new BookResponse(
				2L,
				"출판사",
				"정상판매",
				"제목1",
				"thumbnail1.jpg",
				LocalDate.of(2024, 12, 5),
				BigDecimal.valueOf(10000L),
				10,
				BigDecimal.valueOf(9000L),
				"본문",
				"목차",
				"K123456781",
				false,
				10,
				100,
				1000L,
				List.of("wow", "good"),
				contributorsByRole,
				simpleCategories
			)
		);

		when(bookService.getAllBooks()).thenReturn(allBooks);

		mockMvc.perform(get("/api/books/all"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", Matchers.hasSize(2)));
	}
}
