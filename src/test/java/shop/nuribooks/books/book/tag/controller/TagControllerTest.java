package shop.nuribooks.books.book.tag.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.book.tag.dto.TagRequest;
import shop.nuribooks.books.book.tag.dto.TagResponse;
import shop.nuribooks.books.book.tag.service.TagServiceImpl;

@WebMvcTest(TagController.class)
class TagControllerTest {
	@MockBean
	private TagServiceImpl tagService;

	@Autowired
	private MockMvc mockMvc;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@DisplayName("태그 등록")
	@Test
	void registerTag() throws Exception {
		TagRequest request = TagRequest.builder().name("tag1").build();
		TagResponse response = TagResponse.builder().id(1L).name("tag1").build();

		when(tagService.registerTag(any(TagRequest.class))).thenReturn(response);

		mockMvc.perform(post("/api/books/tags").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").value(1L))
			.andExpect(jsonPath("$.name").value("tag1"));

		verify(tagService).registerTag(any(TagRequest.class));
	}
}