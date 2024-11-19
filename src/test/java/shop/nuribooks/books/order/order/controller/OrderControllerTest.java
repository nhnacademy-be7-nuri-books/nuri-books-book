package shop.nuribooks.books.order.order.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.book.book.dto.BookOrderResponse;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;
import shop.nuribooks.books.common.ControllerTestSupport;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;
import shop.nuribooks.books.member.address.dto.response.AddressResponse;
import shop.nuribooks.books.order.order.dto.OrderInformationResponse;
import shop.nuribooks.books.order.order.dto.OrderTempRegisterRequest;
import shop.nuribooks.books.order.order.dto.OrderTempRegisterResponse;
import shop.nuribooks.books.order.orderDetail.dto.OrderDetailRequest;
import shop.nuribooks.books.order.shipping.dto.ShippingRegisterRequest;

class OrderControllerTest extends ControllerTestSupport {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private OrderTempRegisterRequest orderTempRegisterRequest;
	private OrderTempRegisterResponse orderTempRegisterResponse;
	private OrderInformationResponse memberOrderResponse;
	private OrderInformationResponse CustomerOrderResponse;

	@BeforeEach
	void setUp() {

		OrderDetailRequest orderDetailRequest = new OrderDetailRequest(
			1L,
			2,
			BigDecimal.valueOf(1000),
			true,
			null
		);

		ShippingRegisterRequest shippingRegisterRequest = new ShippingRegisterRequest(
			1L,
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
			LocalDate.now(),
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

		BookContributorInfoResponse contributor1 =
			new BookContributorInfoResponse(1L,
				"고구마", 1L, "저자");
		BookContributorInfoResponse contributor2 =
			new BookContributorInfoResponse(2L, "김철수", 2L, "편집자");

		BookOrderResponse bookOrderResponse = new BookOrderResponse(
			1L,
			"자바 프로그래밍",
			"http://example.com/thumbnail.jpg",
			BigDecimal.valueOf(5000),
			10,
			BigDecimal.valueOf(4500),
			true,
			100,
			List.of(contributor1, contributor2),
			2,
			BigDecimal.valueOf(9000)
		);

		memberOrderResponse = new OrderInformationResponse(
			null,
			null,
			null,
			null,
			BigDecimal.valueOf(1000),
			List.of(new AddressResponse(1L, "집", "11111", "대전 00로 222-11", "", false)),
			List.of(bookOrderResponse),
			1L,
			3000,
			null
		);

		CustomerOrderResponse = new OrderInformationResponse(
			1L,
			"감자",
			"010-4943-3703",
			"test@test.com",
			BigDecimal.ZERO,
			null,
			List.of(bookOrderResponse),
			1L,
			3000,
			null
		);
	}

	@AfterEach
	void tearDown() {
		MemberIdContext.clear();
	}

	@Test
	@DisplayName("주문 폼 정보 가져오기 테스트 - 바로 구매 (회원)")
	void getOrderInformationMemberTest() throws Exception {

		MemberIdContext.setMemberId(1L);

		when(orderService.getMemberOrderInformation(anyLong(), anyLong(), anyInt()))
			.thenReturn(memberOrderResponse);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/{book-id}", 1L)
				.param("quantity", "2"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").doesNotExist())
			.andExpect(jsonPath("$.shippingFee").value(3000));

	}

	@Test
	@DisplayName("주문 폼 정보 가져오기 테스트 - 바로 구매 (비회원)")
	void getOrderInformationCustomerTest() throws Exception {

		when(orderService.getCustomerOrderInformation(anyLong(), anyInt()))
			.thenReturn(CustomerOrderResponse);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/{book-id}", 1L)
				.param("quantity", "2"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").exists())
			.andExpect(jsonPath("$.shippingFee").value(3000));

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
