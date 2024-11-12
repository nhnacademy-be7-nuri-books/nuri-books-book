package shop.nuribooks.books.book.point.contorller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import shop.nuribooks.books.book.point.controller.PointHistoryAdminController;
import shop.nuribooks.books.book.point.dto.response.PointHistoryResponse;
import shop.nuribooks.books.book.point.service.PointHistoryService;

@WebMvcTest(PointHistoryAdminController.class)
public class PointHistoryAdminControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	private PointHistoryService pointHistoryService;

	private List<PointHistoryResponse> pointHistoryResponses = new LinkedList<>();

	@BeforeEach
	void setUp() {
		pointHistoryResponses.add(
			new PointHistoryResponse(1, BigDecimal.TEN, "descriptoi", LocalDateTime.now()));
	}

	@Test
	void getAllTest() throws Exception {
		when(pointHistoryService.getPointHistories(anyLong(), any(), any(),
			any())).thenReturn(
			new PageImpl(pointHistoryResponses, PageRequest.of(0, 3), pointHistoryResponses.size()));

		mockMvc.perform(get("/admin/api/members/1/point-history")
				.queryParam("type", "ALL")
				.queryParam("page", "0")
				.queryParam("size", "3")
				.queryParam("start", LocalDateTime.now().minusDays(30).toString())
				.queryParam("end", LocalDateTime.now().toString()))
			.andExpect(status().isOk());
	}

	@Test
	void deleteTest() throws Exception {
		mockMvc.perform(delete("/admin/api/point-history/1"))
			.andExpect(status().isOk());
	}
}
