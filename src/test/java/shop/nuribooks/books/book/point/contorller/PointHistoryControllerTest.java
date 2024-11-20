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
import org.junit.jupiter.api.Test;
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
public class PointHistoryControllerTest {
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
			new PageImpl<>(pointHistoryResponses, PageRequest.of(0, 3), pointHistoryResponses.size()));
		MemberIdContext.setMemberId(1l);
		mockMvc.perform(get("/api/members/point-history")
				.queryParam("type", "ALL")
				.queryParam("page", "0")
				.queryParam("size", "3")
				.queryParam("start", LocalDate.now().minusDays(30).toString())
				.queryParam("end", LocalDate.now().toString()))
			.andExpect(status().isOk());
	}

	@Test
	void getAllLowerCaseTest() throws Exception {
		when(pointHistoryService.getPointHistories(anyLong(), any(), any(),
			any())).thenReturn(
			new PageImpl<>(pointHistoryResponses, PageRequest.of(0, 3), pointHistoryResponses.size()));
		MemberIdContext.setMemberId(1l);
		mockMvc.perform(get("/api/members/point-history")
				.queryParam("type", "all")
				.queryParam("page", "0")
				.queryParam("size", "3")
				.queryParam("start", LocalDate.now().minusDays(30).toString())
				.queryParam("end", LocalDate.now().toString()))
			.andExpect(status().isOk());
	}

	@Test
	void getWrongTypeTest() throws Exception {
		when(pointHistoryService.getPointHistories(anyLong(), any(), any(),
			any())).thenReturn(
			new PageImpl<>(pointHistoryResponses, PageRequest.of(0, 3), pointHistoryResponses.size()));
		MemberIdContext.setMemberId(1l);
		mockMvc.perform(get("/api/members/point-history")
				.queryParam("type", "alla")
				.queryParam("page", "0")
				.queryParam("size", "3")
				.queryParam("start", LocalDate.now().minusDays(30).toString())
				.queryParam("end", LocalDate.now().toString()))
			.andExpect(status().isOk());
	}

	// @Test
	// void getUsedTest() throws Exception {
	// 	when(pointHistoryService.getUsedPointHistories(anyLong(), any(), any()
	// 		any())).thenReturn(
	// 		new PageImpl<>(pointHistoryResponses, PageRequest.of(0, 3), pointHistoryResponses.size()));
	// 	MemberIdContext.setMemberId(1l);
	// 	mockMvc.perform(get("/api/members/point-history")
	// 			.queryParam("type", "ALL")
	// 			.queryParam("page", "0")
	// 			.queryParam("size", "3")
	// 			.queryParam("start", LocalDate.now().minusDays(30).toString())
	// 			.queryParam("end", LocalDate.now().toString()))
	// 		.andExpect(status().isOk());
	// }
	//
	// @Test
	// void getSavedTest() throws Exception {
	// 	when(pointHistoryService.getPointHistories(anyLong(), any(), any()
	// 		any())).thenReturn(
	// 		new PageImpl<>(pointHistoryResponses, PageRequest.of(0, 3), pointHistoryResponses.size()));
	// 	MemberIdContext.setMemberId(1l);
	// 	mockMvc.perform(get("/api/members/point-history")
	// 			.queryParam("type", "ALL")
	// 			.queryParam("page", "0")
	// 			.queryParam("size", "3")
	// 			.queryParam("start", LocalDate.now().minusDays(30).toString())
	// 			.queryParam("end", LocalDate.now().toString()))
	// 		.andExpect(status().isOk());
	// }
}
