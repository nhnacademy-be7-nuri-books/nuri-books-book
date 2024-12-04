package shop.nuribooks.books.order.wrapping.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.order.wrapping.dto.WrappingPaperRequest;
import shop.nuribooks.books.order.wrapping.dto.WrappingPaperResponse;
import shop.nuribooks.books.order.wrapping.entity.WrappingPaper;
import shop.nuribooks.books.order.wrapping.service.WrappingPaperService;

@WebMvcTest(WrappingPaperAdminController.class)
class WrappingControllerTest {
	@Autowired
	MockMvc mockMvc;
	@MockBean
	WrappingPaperService wrappingPaperService;
	@Autowired
	private ObjectMapper objectMapper;
	private WrappingPaper wrappingPaper;

	private WrappingPaperRequest wrappingPaperRequest;

	@BeforeEach
	void setUp() {
		wrappingPaper = TestUtils.createWrappingPaper();
		TestUtils.setIdForEntity(wrappingPaper, 1L);
		wrappingPaperRequest = new WrappingPaperRequest("이름", "name.com", BigDecimal.TEN);

	}

	@Test
	void getWrappingPapers() throws Exception {
		Pageable pageable = PageRequest.of(0, 3);
		WrappingPaperResponse wrappingPaperResponse = new WrappingPaperResponse(1L, "제목", "image.com", BigDecimal.TEN);

		when(wrappingPaperService.getWrappingPaperResponse(any())).thenReturn(
			new PageImpl<>(List.of(wrappingPaperResponse), pageable, 1));

		mockMvc.perform(get("/api/wrapping-papers")
				.queryParam("page", "0")
				.queryParam("size", "3"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.*", Matchers.hasSize(11)));
	}

	@Test
	void registerWrappingPaper() throws Exception {
		when(wrappingPaperService.registerWrappingPaper(wrappingPaperRequest)).thenReturn(wrappingPaper);

		mockMvc.perform(post("/api/wrapping-papers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(wrappingPaperRequest)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.*", Matchers.hasSize(2)));
	}

	@Test
	void updateWrappingPaper() throws Exception {
		when(wrappingPaperService.updateWrappingPaper(anyLong(), any())).thenReturn(wrappingPaper);

		mockMvc.perform(put("/api/wrapping-papers/" + wrappingPaper.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(wrappingPaperRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.*", Matchers.hasSize(2)));
	}

	@Test
	void deleteWrappingPaper() throws Exception {
		mockMvc.perform(delete("/api/wrapping-papers/" + wrappingPaper.getId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.*", Matchers.hasSize(2)));
	}
}
