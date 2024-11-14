package shop.nuribooks.books.order.order.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.common.threadlocal.MemberIdContext;
import shop.nuribooks.books.order.order.dto.OrderTempRegisterRequest;
import shop.nuribooks.books.order.order.dto.OrderTempRegisterResponse;
import shop.nuribooks.books.order.order.service.OrderService;
import shop.nuribooks.books.order.orderDetail.dto.OrderDetailRequest;
import shop.nuribooks.books.order.shipping.dto.ShippingPolicyRequest;
import shop.nuribooks.books.order.shipping.dto.ShippingRegisterRequest;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OrderService orderService;

	@Autowired
	private ObjectMapper objectMapper;

	private OrderTempRegisterRequest orderTempRegisterRequest;
	private OrderTempRegisterResponse orderTempRegisterResponse;

	@BeforeEach
	void setUp() {

		OrderDetailRequest orderDetailRequest = new OrderDetailRequest(
			1L,
			2,
			BigDecimal.valueOf(1000),
			true,
			null
		);

		ShippingPolicyRequest shippingPolicyRequest = new ShippingPolicyRequest(1L);
		ShippingRegisterRequest shippingRegisterRequest = new ShippingRegisterRequest(
			shippingPolicyRequest,
			"홍길동",
			"서울특별시 강남구 테헤란로 123",
			"101호",
			"06234",
			"01012345678",
			"김철수",
			"01087654321"
		);

		List<OrderDetailRequest> orderDetailRequestList = new ArrayList<>();
		orderDetailRequestList.add(orderDetailRequest);

		orderTempRegisterRequest = new OrderTempRegisterRequest(
			BigDecimal.valueOf(1000),
			BigDecimal.ZERO,
			LocalDateTime.now(),
			orderDetailRequestList,
			shippingRegisterRequest,
			null,
			BigDecimal.ZERO,
			null,
			null
		);

		orderTempRegisterResponse = new OrderTempRegisterResponse(
			1L,
			"주문",
			BigDecimal.valueOf(1000),
			BigDecimal.valueOf(1000),
			LocalDateTime.now()
		);

	}

	@AfterEach
	void tearDown() {
		MemberIdContext.clear();
	}

	@Test
	@DisplayName("회원 주문 테스트")
	void registerTempOrderForMember() throws Exception {

		MemberIdContext.setMemberId(1L);

		when(orderService.registerTempOrderForMember(any(Long.class), any(OrderTempRegisterRequest.class)))
			.thenReturn(orderTempRegisterResponse);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/orders")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(orderTempRegisterRequest)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.orderId").value(1));
	}

	@Test
	@DisplayName("비회원 주문 테스트")
	void registerTempOrderForCustomer() throws Exception {
		when(orderService.registerTempOrderForCustomer(any(OrderTempRegisterRequest.class)))
			.thenReturn(orderTempRegisterResponse);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/orders")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(orderTempRegisterRequest)))
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("$.orderId").value(1));
	}
}
