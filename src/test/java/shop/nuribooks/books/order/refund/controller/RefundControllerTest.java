package shop.nuribooks.books.order.refund.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.order.refund.dto.request.RefundRequest;
import shop.nuribooks.books.order.refund.service.RefundService;

@WebMvcTest(RefundController.class)
class RefundControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private RefundService refundService;

	@DisplayName("환불 정보를 조회한다")
	@Test
	void getRefundResponse() throws Exception {
		// given
		Long orderId = 1L;

		// when
		mockMvc.perform(get("/api/orders/refunds/{order-id}", orderId))
			.andExpect(status().isOk());

		// then
		verify(refundService).getRefundInfoResponse(orderId);
	}

	@DisplayName("환불 정보를 조회한다")
	@Test
	void registerRefund() throws Exception {
		// given
		Long orderId = 1L;
		BigDecimal refundAmount = BigDecimal.valueOf(10000L);
		String reason = "reason";
		RefundRequest refundRequest = new RefundRequest(refundAmount, reason);

		// when
		mockMvc.perform(post("/api/orders/refunds/{order-id}", orderId)
				.content(objectMapper.writeValueAsString(refundRequest))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		// then
		verify(refundService).refund(orderId, refundRequest);
	}

}
