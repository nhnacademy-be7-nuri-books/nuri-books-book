package shop.nuribooks.books.book.booktag.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
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

import shop.nuribooks.books.book.TestUtils;
import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.entity.BookStateEnum;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.booktag.dto.BookTagGetResponse;
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

		TestUtils.setIdForEntity(book, 1L);
		TestUtils.setIdForEntity(book1, 2L);

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

		BookResponse bookResponse1 = BookResponse.of(book);
		BookResponse bookResponse2 = BookResponse.of(book1);

		List<BookResponse> responses = List.of(bookResponse1, bookResponse2);
		when(bookTagService.getBooksByTagId(tagId)).thenReturn(responses);

		// When & Then
		mockMvc.perform(get("/api/book-tags/tag/{tagId}", tagId)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id").value(book.getId()))
				.andExpect(jsonPath("$[0].title").value(book.getTitle()))
				.andExpect(jsonPath("$[0].publisher.id").value(book.getPublisherId().getId()))
				.andExpect(jsonPath("$[0].publisher.name").value(book.getPublisherId().getName()))
				.andExpect(jsonPath("$[1].id").value(book1.getId()))
				.andExpect(jsonPath("$[1].title").value(book1.getTitle()))
				.andExpect(jsonPath("$[1].publisher.id").value(book1.getPublisherId().getId()))
				.andExpect(jsonPath("$[1].publisher.name").value(book1.getPublisherId().getName()))
				.andExpect(jsonPath("$[0].state").value(book.getState().getKorName()))
				.andExpect(jsonPath("$[0].thumbnailImageUrl").value(book.getThumbnailImageUrl()))
				.andExpect(jsonPath("$[0].detailImageUrl").value(book.getDetailImageUrl()))
				.andExpect(jsonPath("$[0].publicationDate").value(book.getPublicationDate().toString()))
				.andExpect(jsonPath("$[0].price").value(book.getPrice()))
				.andExpect(jsonPath("$[0].discountRate").value(book.getDiscountRate()))
				.andExpect(jsonPath("$[0].description").value(book.getDescription()))
				.andExpect(jsonPath("$[0].contents").value(book.getContents()))
				.andExpect(jsonPath("$[0].isbn").value(book.getIsbn()))
				.andExpect(jsonPath("$[0].isPackageable").value(book.isPackageable()))
				.andExpect(jsonPath("$[0].likeCount").value(book.getLikeCount()))
				.andExpect(jsonPath("$[0].stock").value(book.getStock()))
				.andExpect(jsonPath("$[0].viewCount").value(book.getViewCount()));
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
