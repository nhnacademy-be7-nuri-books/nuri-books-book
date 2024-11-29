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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.book.book.dto.BookOrderResponse;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;
import shop.nuribooks.books.member.address.dto.response.AddressResponse;
import shop.nuribooks.books.member.customer.dto.CustomerDto;
import shop.nuribooks.books.order.order.dto.request.OrderRegisterRequest;
import shop.nuribooks.books.order.order.dto.response.OrderInformationResponse;
import shop.nuribooks.books.order.order.dto.response.OrderRegisterResponse;
import shop.nuribooks.books.order.order.service.OrderService;
import shop.nuribooks.books.order.orderdetail.dto.OrderDetailRequest;
import shop.nuribooks.books.order.shipping.dto.ShippingRegisterRequest;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

	@MockBean
	protected OrderService orderService;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	private OrderRegisterRequest orderTempRegisterRequest;
	private OrderRegisterResponse orderTempRegisterResponse;
	private OrderInformationResponse memberOrderResponse;
	private OrderInformationResponse customerOrderResponse;

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

		CustomerDto customerDto = new CustomerDto(
			1L,
			"홍길동",
			"01012345678",
			"abd@test.com",
			BigDecimal.valueOf(5000),
			List.of(new AddressResponse(1L, "집", "11111", "대전 00로 222-11", "", false))
		);

		orderTempRegisterRequest = new OrderRegisterRequest(
			BigDecimal.valueOf(1000),
			BigDecimal.ZERO,
			LocalDate.now(),
			orderDetailRequestList,
			shippingRegisterRequest,
			null,
			BigDecimal.ZERO,
			null,
			null,
			null
		);

		orderTempRegisterResponse = new OrderRegisterResponse(
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
			customerDto,
			null,
			null,
			null,
			null,
			null
		);

		customerOrderResponse = new OrderInformationResponse(
			null,
			null,
			null,
			null,
			null,
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
			.andExpect(jsonPath("$.customer.name").isNotEmpty());

	}

	@Test
	@DisplayName("주문 폼 정보 가져오기 테스트 - 바로 구매 (비회원)")
	void getOrderInformationCustomerTest() throws Exception {

		when(orderService.getCustomerOrderInformation(anyLong(), anyInt()))
			.thenReturn(customerOrderResponse);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/{book-id}", 1L)
				.param("quantity", "2"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.customer").isEmpty());

	}

	@Test
	@DisplayName("회원 주문 테스트")
	void registerTempOrderForMember() throws Exception {

		MemberIdContext.setMemberId(1L);

		when(orderService.registerTempOrderForMember(any(Long.class), any(OrderRegisterRequest.class)))
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
		when(orderService.registerTempOrderForCustomer(any(OrderRegisterRequest.class)))
			.thenReturn(orderTempRegisterResponse);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/orders")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(orderTempRegisterRequest)))
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("$.orderId").value(1));
	}
}
