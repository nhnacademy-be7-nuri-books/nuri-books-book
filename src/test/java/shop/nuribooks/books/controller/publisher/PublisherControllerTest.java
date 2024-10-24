package shop.nuribooks.books.controller.publisher;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.dto.publisher.PublisherRequest;
import shop.nuribooks.books.dto.publisher.PublisherResponse;
import shop.nuribooks.books.service.publisher.PublisherServiceImpl;

@WebMvcTest(PublisherController.class)
class PublisherControllerTest {

	@Autowired
	private PublisherController publisherController;

	@MockBean
	private PublisherServiceImpl publisherService;

	private MockMvc mockMvc;
	private final ObjectMapper objectMapper = new ObjectMapper();  // JSON 직렬화에 사용

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(publisherController).build();
	}

	@DisplayName("출판사 등록")
	@Test
	void registerPublisher() throws Exception {

		PublisherRequest request = registerRequest();
		PublisherResponse response = registerResponse();

		when(publisherService.registerPublisher(any(PublisherRequest.class))).thenReturn(response);

		mockMvc.perform(post("/api/publishers").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.name").value("publisher1"));

		verify(publisherService).registerPublisher(any(PublisherRequest.class));

	}

	private PublisherRequest registerRequest() {
		return PublisherRequest.builder().name("publisher1").build();
	}

	private PublisherResponse registerResponse() {
		return PublisherResponse.builder().name("publisher1").build();
	}

}