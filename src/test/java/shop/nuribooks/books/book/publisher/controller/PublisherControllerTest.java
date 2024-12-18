package shop.nuribooks.books.book.publisher.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.book.publisher.dto.PublisherRequest;
import shop.nuribooks.books.book.publisher.dto.PublisherResponse;
import shop.nuribooks.books.book.publisher.entity.Publisher;
import shop.nuribooks.books.book.publisher.service.PublisherServiceImpl;

@WebMvcTest(PublisherController.class)
@AutoConfigureMockMvc
class PublisherControllerTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	@Autowired
	private PublisherController publisherController;
	@MockBean
	private PublisherServiceImpl publisherService;
	@Autowired
	private MockMvc mockMvc;

	@DisplayName("출판사 등록")
	@Test
	void registerPublisher() throws Exception {

		PublisherRequest request = registerRequest();
		PublisherResponse response = registerResponse();

		when(publisherService.registerPublisher(any(PublisherRequest.class))).thenReturn(response);

		mockMvc.perform(post("/api/books/publishers").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated());

		verify(publisherService).registerPublisher(any(PublisherRequest.class));

	}

	@DisplayName("모든 출판사 조회")
	@Test
	void getAllPublisher() throws Exception {
		// given
		List<PublisherResponse> responses = List.of(
			new PublisherResponse(1L, "publisher1"),
			new PublisherResponse(2L, "publisher2")
		);

		Page<PublisherResponse> pageResponses = new PageImpl<>(responses);
		Pageable pageable = PageRequest.of(0, 10);

		when(publisherService.getAllPublisher(pageable)).thenReturn(pageResponses);

		// when & then
		mockMvc.perform(get("/api/books/publishers")
				.contentType(MediaType.APPLICATION_JSON)
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.content[0].name").value("publisher1"))
			.andExpect(jsonPath("$.content[1].name").value("publisher2"));

		verify(publisherService).getAllPublisher(pageable);
	}

	@DisplayName("출판사 조회 성공")
	@Test
	void getPublisher() throws Exception {
		// given
		Long publisherId = 1L;
		PublisherResponse response = new PublisherResponse(publisherId, "publisher1");

		when(publisherService.getPublisher(publisherId)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/api/books/publishers/{publisher-id}", publisherId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(publisherId));

		verify(publisherService).getPublisher(publisherId);
	}

	@DisplayName("출판사 삭제 성공")
	@Test
	void deletePublisher() throws Exception {
		// given
		Long publisherId = 1L;

		// when & then
		mockMvc.perform(delete("/api/books/publishers/{publisher-id}", publisherId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());

		verify(publisherService).deletePublisher(publisherId);
	}

	@DisplayName("출판사 수정 성공")
	@Test
	void updatePublisher() throws Exception {
		// given
		Long publisherId = 1L;
		PublisherRequest request = editRequest();
		PublisherResponse response = PublisherResponse.of(new Publisher(publisherId, "update"));

		when(publisherService.updatePublisher(eq(publisherId), any(PublisherRequest.class)))
			.thenReturn(response);

		// when & then
		mockMvc.perform(put("/api/books/publishers/{publisher-id}", publisherId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());

		verify(publisherService).updatePublisher(eq(publisherId), any(PublisherRequest.class));
	}

	private PublisherRequest registerRequest() {
		return PublisherRequest.builder().name("publisher1").build();
	}

	private PublisherRequest editRequest() {
		return PublisherRequest.builder().name("update").build();
	}

	private PublisherResponse registerResponse() {
		return PublisherResponse.builder().name("publisher1").build();
	}

}