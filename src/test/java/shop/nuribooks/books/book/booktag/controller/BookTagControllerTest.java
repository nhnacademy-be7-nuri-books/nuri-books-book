package shop.nuribooks.books.book.booktag.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.book.entitiy.BookStateEnum;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.booktag.dto.BookTagRequest;
import shop.nuribooks.books.book.booktag.dto.BookTagRegisterResponse;
import shop.nuribooks.books.book.booktag.service.BookTagService;
import shop.nuribooks.books.book.publisher.entitiy.Publisher;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BookTagController.class)
class BookTagControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookTagService bookTagService;

	@Autowired
	private ObjectMapper objectMapper;

	@Mock
	private BookRepository bookRepository;

	private Book book;
	private Book book1;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		Publisher publisher = new Publisher(1L, "Sample Publisher");

		book = Book.builder()
				.publisherId(publisher)
				.state(BookStateEnum.NEW)
				.title("Sample Book")
				.thumbnailImageUrl("https://example.com/thumbnail.jpg")
				.detailImageUrl("https://example.com/detail.jpg")
				.publicationDate(LocalDate.now())
				.price(BigDecimal.valueOf(29.99))
				.discountRate(10)
				.description("Sample description.")
				.contents("Sample contents.")
				.isbn("978-3-16-148410-0")
				.isPackageable(true)
				.stock(100)
				.likeCount(0)
				.viewCount(0L)
				.build();

		book1 = Book.builder()
				.publisherId(publisher)
				.state(BookStateEnum.NEW)
				.title("Sample Book 1")
				.thumbnailImageUrl("https://example.com/thumbnail1.jpg")
				.detailImageUrl("https://example.com/detail1.jpg")
				.publicationDate(LocalDate.now())
				.price(BigDecimal.valueOf(19.99))
				.discountRate(5)
				.description("Sample description for book 1.")
				.contents("Sample contents for book 1.")
				.isbn("978-3-16-148410-1")
				.isPackageable(true)
				.stock(50)
				.likeCount(0)
				.viewCount(1L)
				.build();

		book = bookRepository.save(book);
		book1 = bookRepository.save(book1);

	}
	@DisplayName("도서 태그 등록")
	@Test
	void registerTagToBook() throws Exception {
		// Given
		BookTagRequest request = new BookTagRequest(1L, List.of(2L, 3L));
		BookTagRegisterResponse response = new BookTagRegisterResponse(1L, 1L, List.of(2L, 3L));

		// Mocking the service call
		when(bookTagService.registerTagToBook(any(BookTagRequest.class)))
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
		//
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
		//
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
		//
		BookTagRequest request = new BookTagRequest(1L, null);

		// When & Then
		mockMvc.perform(post("/api/book-tags")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("tagId는 필수 입력 항목입니다"));
	}

}
