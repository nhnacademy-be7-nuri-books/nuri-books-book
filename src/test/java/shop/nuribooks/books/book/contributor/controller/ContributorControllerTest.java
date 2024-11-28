package shop.nuribooks.books.book.contributor.controller;

import static org.mockito.ArgumentMatchers.*;
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

import shop.nuribooks.books.book.contributor.dto.ContributorRequest;
import shop.nuribooks.books.book.contributor.dto.ContributorResponse;
import shop.nuribooks.books.book.contributor.service.ContributorServiceImpl;

@WebMvcTest(ContributorController.class)
@AutoConfigureMockMvc
class ContributorControllerTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	@MockBean
	private ContributorServiceImpl contributorService;
	@Autowired
	private MockMvc mockMvc;

	@DisplayName("기여자 등록")
	@Test
	void registerContributor() throws Exception {
		// given
		ContributorRequest request = ContributorRequest.builder().name("Kim").build();
		ContributorResponse response = ContributorResponse.builder().id(1L).name("Kim").build();

		when(contributorService.registerContributor(any(ContributorRequest.class))).thenReturn(response);

		// when & then
		mockMvc.perform(post("/api/contributors")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated());

		verify(contributorService).registerContributor(any(ContributorRequest.class));
	}

	@DisplayName("모든 출판사 조회")
	@Test
	void getAllContributor() throws Exception {
		// given
		List<ContributorResponse> responses = List.of(
			new ContributorResponse(1L, "contributor1"),
			new ContributorResponse(2L, "contributor2")
		);

		Page<ContributorResponse> pageResponses = new PageImpl<>(responses);
		Pageable pageable = PageRequest.of(0, 10);

		when(contributorService.getAllContributors(pageable)).thenReturn(pageResponses);

		// when & then
		mockMvc.perform(get("/api/contributors")
				.contentType(MediaType.APPLICATION_JSON)
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.content[0].name").value("contributor1"))
			.andExpect(jsonPath("$.content[1].name").value("contributor2"));

		verify(contributorService).getAllContributors(pageable);
	}

	@DisplayName("기여자 수정")
	@Test
	void updateContributor() throws Exception {
		// given
		Long contributorId = 1L;
		ContributorRequest request = ContributorRequest.builder().name("Lee").build();
		ContributorResponse response = ContributorResponse.builder().id(1L).name("Lee").build();

		when(contributorService.updateContributor(eq(contributorId), any(ContributorRequest.class))).thenReturn(
			response);

		// when & then
		mockMvc.perform(put("/api/contributors/{contributor-id}", contributorId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());
		verify(contributorService).updateContributor(eq(contributorId), any(ContributorRequest.class));
	}

	@DisplayName("기여자 조회")
	@Test
	void getContributor() throws Exception {
		// given
		Long contributorId = 1L;
		ContributorResponse response = ContributorResponse.builder().id(1L).name("Kim").build();

		when(contributorService.getContributor(contributorId)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/api/contributors/{contributor-id}", contributorId)
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
		mockMvc.perform(delete("/api/contributors/{contributor-id}", contributorId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());

		verify(contributorService).deleteContributor(contributorId);
	}
}
