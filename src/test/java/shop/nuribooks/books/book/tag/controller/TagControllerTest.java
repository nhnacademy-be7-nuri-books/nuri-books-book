package shop.nuribooks.books.book.tag.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

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

	@DisplayName("모든 태그 조회")
	@Test
	void getAllTags() throws Exception {
		TagResponse tag1 = TagResponse.builder().id(1L).name("tag1").build();
		TagResponse tag2 = TagResponse.builder().id(2L).name("tag2").build();
		List<TagResponse> response = List.of(tag1, tag2);

		when(tagService.getAllTags()).thenReturn(response);

		mockMvc.perform(get("/api/books/tags")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].id").value(1L))
			.andExpect(jsonPath("$[0].name").value("tag1"))
			.andExpect(jsonPath("$[1].id").value(2L))
			.andExpect(jsonPath("$[1].name").value("tag2"));

		verify(tagService, times(1)).getAllTags();

	}

	@DisplayName("특정 태그 조회")
	@Test
	void getTag() throws Exception {
		Long id = 1L;
		TagResponse response = TagResponse.builder().id(id).name("tag1").build();

		when(tagService.getTag(id)).thenReturn(response);

		mockMvc.perform(get("/api/books/tags/{tagId}", id)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("tag1"));

		verify(tagService).getTag(id);

	}

	@DisplayName("특정 태그 수정")
	@Test
	void updateTag() throws Exception {
		Long id = 1L;
		TagRequest request = TagRequest.builder().name("tag1").build();
		TagResponse response = TagResponse.builder().id(id).name("update").build();

		when(tagService.updateTag(id, request)).thenReturn(response);

		mockMvc.perform(put("/api/books/tags/{tagId}", id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(content().json("{\"name\":\"update\"}"));

		verify(tagService).updateTag(id, request);

	}
}