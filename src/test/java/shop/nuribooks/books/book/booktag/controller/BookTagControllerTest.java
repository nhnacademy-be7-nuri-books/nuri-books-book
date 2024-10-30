package shop.nuribooks.books.book.booktag.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.book.booktag.dto.BookTagRequest;
import shop.nuribooks.books.book.booktag.dto.BookTagResponse;
import shop.nuribooks.books.book.booktag.service.BookTagService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BookTagController.class)
class BookTagControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookTagService bookTagService;

	@DisplayName("도서 태그 등록")
	@Test
	void registerTagToBook() throws Exception {
		// Given
		BookTagRequest request = new BookTagRequest(1L, List.of(2L, 3L));
		BookTagResponse response = new BookTagResponse(1L, 1L, List.of(2L, 3L));

		// Mocking the service call
		when(bookTagService.registerTagToBook(any(BookTagRequest.class)))
			.thenReturn(response);

		// When & Then
		mockMvc.perform(post("/api/book-tags")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.bookId").value(1L))
			.andExpect(jsonPath("$.tagId").isArray())
			.andExpect(jsonPath("$.tagId[0]").value(2L))
			.andExpect(jsonPath("$.tagId[1]").value(3L));
	}
}
