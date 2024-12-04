package shop.nuribooks.books.book.booktag.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.booktag.dto.BookTagGetResponse;
import shop.nuribooks.books.book.booktag.dto.BookTagRegisterResponse;
import shop.nuribooks.books.book.booktag.dto.BookTagRequest;
import shop.nuribooks.books.book.booktag.service.BookTagService;

@WebMvcTest(BookTagController.class)
class BookTagControllerTest {

	@MockBean
	protected BookTagService bookTagService;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Mock
	private BookRepository bookRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@DisplayName("도서 태그 등록")
	@Test
	void registerTagToBook() throws Exception {
		// Given
		BookTagRequest request = new BookTagRequest(1L, List.of(2L, 3L));
		BookTagRegisterResponse response = new BookTagRegisterResponse(1L, 1L, List.of(2L, 3L));

		// Mocking the service call
		when(bookTagService.registerTagToBook(any()))
			.thenReturn(response);

		// When & Then
		mockMvc.perform(post("/api/book-tags")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.bookId").value(1L))
			.andExpect(jsonPath("$.tagIds").isArray())
			.andExpect(jsonPath("$.tagIds[0]").value(2L))
			.andExpect(jsonPath("$.tagIds[1]").value(3L));
	}

	@Test
	@DisplayName("유효성 검사 실패 - 음수 bookId")
	void registerTagToBook_InvalidBookId() throws Exception {
		// Given
		BookTagRequest request = new BookTagRequest(-1L, List.of(1L, 2L));

		// When & Then
		mockMvc.perform(post("/api/book-tags")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("bookId는 양수여야 합니다"));
	}

	@Test
	@DisplayName("유효성 검사 실패 - 음수 tagId")
	void registerTagToBook_InvalidTagId() throws Exception {
		BookTagRequest request = new BookTagRequest(1L, List.of(-1L, 2L));

		// When & Then
		mockMvc.perform(post("/api/book-tags")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("tagId는 양수여야 합니다"));
	}

	@Test
	@DisplayName("유효성 검사 실패 - Null bookId")
	void registerTagToBook_NullBookId() throws Exception {
		BookTagRequest request = new BookTagRequest(null, List.of(1L, 2L));

		// When & Then
		mockMvc.perform(post("/api/book-tags")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("bookId는 필수 입력 항목입니다"));
	}

	@Test
	@DisplayName("유효성 검사 실패 - Null tagId")
	void registerTagToBook_NullTagId() throws Exception {
		BookTagRequest request = new BookTagRequest(1L, null);

		// When & Then
		mockMvc.perform(post("/api/book-tags")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("tagId는 필수 입력 항목입니다"));
	}

	@DisplayName("도서 태그 조회 성공")
	@Test
	void getBookTag_Success() throws Exception {
		// Given
		Long bookId = 1L;
		Long bookTagId = 2L; // 가상의 bookTagId 생성
		List<String> tagNames = Collections.singletonList("Sample Tag"); // 가상의 태그 이름 생성
		BookTagGetResponse response = BookTagGetResponse.of(bookTagId, bookId, tagNames);

		when(bookTagService.getBookTag(bookId)).thenReturn(response);

		// When & Then
		mockMvc.perform(get("/api/book-tags/book/{bookId}", bookId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.bookTagId").value(bookTagId)) // bookTagId 확인
			.andExpect(jsonPath("$.bookId").value(bookId)) // bookId 확인
			.andExpect(jsonPath("$.tagNames[0]").value("Sample Tag")); // tagNames 확인
	}

	@DisplayName("태그에 해당하는 도서 조회 성공")
	@Test
	void getBooksByTagId() throws Exception {
		// Given
		Long tagId = 1L;

		List<BookResponse> responses = List.of();
		when(bookTagService.getBooksByTagId(tagId)).thenReturn(responses);

		// When & Then
		mockMvc.perform(get("/api/book-tags/tag/{tagId}", tagId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.*", hasSize(0)));
	}

	@DisplayName("도서 태그 삭제 성공")
	@Test
	void deleteBookTag() throws Exception {
		// Given
		Long bookTagId = 1L;

		// When & Then
		mockMvc.perform(delete("/api/book-tags/{bookTagId}", bookTagId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());

		verify(bookTagService, times(1)).deleteBookTag(bookTagId);
	}
}
