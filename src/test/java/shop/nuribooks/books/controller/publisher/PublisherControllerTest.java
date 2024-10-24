package shop.nuribooks.books.controller.publisher;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
@AutoConfigureMockMvc
class PublisherControllerTest {

	@Autowired
	private PublisherController publisherController;

	@MockBean
	private PublisherServiceImpl publisherService;

	@Autowired
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

	@DisplayName("모든 출판사 조회")
	@Test
	void getAllPublisher() throws Exception {
		// given
		List<PublisherResponse> responses = List.of(
			new PublisherResponse(1L, "publisher1"),
			new PublisherResponse(2L, "publisher2")
		);

		when(publisherService.getAllPublisher()).thenReturn(responses);

		// when & then
		mockMvc.perform(get("/api/publishers")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$[0].name").value("publisher1"))
			.andExpect(jsonPath("$[1].name").value("publisher2"));

		verify(publisherService).getAllPublisher();
	}

	@DisplayName("출판사 조회 성공")
	@Test
	void getPublisher() throws Exception {
		// given
		String publisherName = "publisher1";
		PublisherResponse response = new PublisherResponse(1L, publisherName);

		when(publisherService.getPublisher(publisherName)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/api/publishers/{publisherName}", publisherName)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value(publisherName));

		verify(publisherService).getPublisher(publisherName);
	}

	@DisplayName("출판사 삭제 성공")
	@Test
	void deletePublisher() throws Exception {
		// given
		String publisherName = "publisher1";

		// when & then
		mockMvc.perform(delete("/api/publishers/{publisherName}", publisherName)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		verify(publisherService).deletePublisher(publisherName);
	}

	private PublisherRequest registerRequest() {
		return PublisherRequest.builder().name("publisher1").build();
	}

	private PublisherResponse registerResponse() {
		return PublisherResponse.builder().name("publisher1").build();
	}

}