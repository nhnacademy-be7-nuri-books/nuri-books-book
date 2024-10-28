package shop.nuribooks.books.book.contributor.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.book.contributor.dto.ContributorRequest;
import shop.nuribooks.books.book.contributor.dto.ContributorResponse;
import shop.nuribooks.books.book.contributor.service.ContributorServiceImpl;

@WebMvcTest(ContributorController.class)
@AutoConfigureMockMvc
class ContributorControllerTest {

	@MockBean
	private ContributorServiceImpl contributorService;

	@Autowired
	private MockMvc mockMvc;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@DisplayName("기여자 등록")
	@Test
	void registerContributor() throws Exception {
		// given
		ContributorRequest request = new ContributorRequest("Kim");
		ContributorResponse response = new ContributorResponse(1L, "Kim");

		when(contributorService.registerContributor(any(ContributorRequest.class))).thenReturn(response);

		// when & then
		mockMvc.perform(post("/api/contributors")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.name").value("Kim"));

		verify(contributorService).registerContributor(any(ContributorRequest.class));
	}

	@DisplayName("기여자 수정")
	@Test
	void updateContributor() throws Exception {
		// given
		Long contributorId = 1L;
		ContributorRequest request = new ContributorRequest("Lee");
		ContributorResponse response = new ContributorResponse(1L, "Lee");

		when(contributorService.updateContributor(eq(contributorId), any(ContributorRequest.class))).thenReturn(response);

		// when & then
		mockMvc.perform(put("/api/contributors/{contributorId}", contributorId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").value(contributorId))
			.andExpect(jsonPath("$.name").value("Lee"));

		verify(contributorService).updateContributor(eq(contributorId), any(ContributorRequest.class));
	}

	@DisplayName("기여자 조회")
	@Test
	void getContributor() throws Exception {
		// given
		Long contributorId = 1L;
		ContributorResponse response = new ContributorResponse(1L, "Kim");

		when(contributorService.getContributor(contributorId)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/api/contributors/{contributorId}", contributorId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("Kim"));

		verify(contributorService).getContributor(contributorId);
	}

	@DisplayName("기여자 삭제")
	@Test
	void deleteContributor() throws Exception {
		// given
		Long contributorId = 1L;

		// when & then
		mockMvc.perform(delete("/api/contributors/{contributorId}", contributorId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		verify(contributorService).deleteContributor(contributorId);
	}
}
