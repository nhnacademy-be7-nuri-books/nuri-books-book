package shop.nuribooks.books.book.point.contorller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import shop.nuribooks.books.book.point.controller.PointHistoryController;
import shop.nuribooks.books.book.point.dto.response.PointHistoryResponse;
import shop.nuribooks.books.book.point.service.PointHistoryService;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;

@WebMvcTest(PointHistoryController.class)
class PointHistoryControllerTest {
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

	@ParameterizedTest
	@ValueSource(strings = {"ALL", "all", "alla"})
	void getAllTest(String value) throws Exception {
		when(pointHistoryService.getPointHistories(anyLong(), any(), any(),
			any())).thenReturn(
			new PageImpl<>(pointHistoryResponses, PageRequest.of(0, 3), pointHistoryResponses.size()));
		MemberIdContext.setMemberId(1L);
		mockMvc.perform(get("/api/members/point-history")
				.queryParam("type", value)
				.queryParam("page", "0")
				.queryParam("size", "3")
				.queryParam("start", LocalDate.now().minusDays(30).toString())
				.queryParam("end", LocalDate.now().toString()))
			.andExpect(status().isOk());
	}
}
