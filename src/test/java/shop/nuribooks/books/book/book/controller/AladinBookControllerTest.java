package shop.nuribooks.books.book.book.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.book.book.dto.response.AladinBookListItemResponse;
import shop.nuribooks.books.book.book.service.AladinBookService;

@WebMvcTest(AladinBookController.class)
public class AladinBookControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private AladinBookService aladinBookService;

	@Test
	void getAladinBookList() throws Exception {

		List<AladinBookListItemResponse> aladinBooks = List.of(
			new AladinBookListItemResponse(
				"책1",
				"일 (지은이)",
				"2024-12-05",
				"Description1",
				"K123456789",
				BigDecimal.valueOf(10000),
				"정상판매",
				"cover1.jpg",
				"Publisher1",
				"국내도서 > 국내소설"
			),
			new AladinBookListItemResponse(
				"책2",
				"이 (지은이)",
				"2024-12-05",
				"Description2",
				"K234567890",
				BigDecimal.valueOf(15000),
				"정상판매",
				"cover2.jpg",
				"Publisher2",
				"국내도서 > 국내문학"
			)
		);

		when(aladinBookService.getNewBooks(any(), any(), anyInt()))
			.thenReturn(aladinBooks);

		mockMvc.perform(get("/api/books/aladin")
				.param("queryType", "ItemNewAll")
				.param("searchTarget", "ItemNewAll")
				.param("maxResults", "10")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(2));
	}

	@Test
	void getBookByIsbn() throws Exception {
		AladinBookListItemResponse response = new AladinBookListItemResponse(
			"책1",
			"일 (지은이)",
			"2024-12-05",
			"Description1",
			"K123456789",
			BigDecimal.valueOf(10000),
			"정상판매",
			"cover1.jpg",
			"Publisher1",
			"국내도서 > 국내소설"
		);

		when(aladinBookService.getBookByIsbnWithAladin(anyString())).thenReturn(response);

		mockMvc.perform(get("/api/books/aladin/{isbn}", "1234567890123")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isbn").value("K123456789"));
	}
}
