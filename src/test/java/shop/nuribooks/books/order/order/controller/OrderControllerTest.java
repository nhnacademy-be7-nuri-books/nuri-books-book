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
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.common.message.ResponseMessage;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;
import shop.nuribooks.books.member.address.dto.response.AddressResponse;
import shop.nuribooks.books.member.customer.dto.CustomerDto;
import shop.nuribooks.books.order.order.dto.OrderCancelDto;
import shop.nuribooks.books.order.order.dto.OrderSummaryDto;
import shop.nuribooks.books.order.order.dto.request.OrderCancelRequest;
import shop.nuribooks.books.order.order.dto.request.OrderListPeriodRequest;
import shop.nuribooks.books.order.order.dto.request.OrderRegisterRequest;
import shop.nuribooks.books.order.order.dto.response.OrderInformationResponse;
import shop.nuribooks.books.order.order.dto.response.OrderListResponse;
import shop.nuribooks.books.order.order.dto.response.OrderRegisterResponse;
import shop.nuribooks.books.order.order.service.OrderService;
import shop.nuribooks.books.order.orderdetail.dto.OrderDetailRequest;
import shop.nuribooks.books.order.orderdetail.dto.OrderDetailResponse;
import shop.nuribooks.books.order.orderdetail.entity.OrderState;
import shop.nuribooks.books.order.shipping.dto.ShippingInfoDto;
import shop.nuribooks.books.order.shipping.dto.ShippingRegisterRequest;
import shop.nuribooks.books.payment.payment.dto.PaymentRequest;

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
	private PaymentRequest paymentRequest;
	private Page<OrderListResponse> orderListPage;
	private OrderDetailResponse orderDetailResponse;
	private OrderCancelDto orderCancelDto;

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

		paymentRequest = new PaymentRequest(
			"TEST-ORDER-0001",
			"10000",
			"PAYMENT-TEST-KEY"
		);

		OrderListResponse orderListResponse1 = new OrderListResponse(
			1L,
			LocalDateTime.now(),
			"주문 1",
			BigDecimal.valueOf(10000),
			"10",
			OrderState.PENDING
		);

		OrderListResponse orderListResponse2 = new OrderListResponse(
			2L,
			LocalDateTime.now(),
			"주문 2",
			BigDecimal.valueOf(20000),
			"20",
			OrderState.PAID
		);

		List<OrderListResponse> orders = List.of(orderListResponse1, orderListResponse2);
		orderListPage = new PageImpl<>(orders, PageRequest.of(0, 10), orders.size());

		orderDetailResponse = new OrderDetailResponse(
			new OrderSummaryDto(
				"주문 1",
				LocalDateTime.now(),
				null
			),
			null,
			new ShippingInfoDto(
				"홍길동",
				"01033223322",
				"대전",
				"2층",
				"232112"
			),
			null
		);

		orderCancelDto = new OrderCancelDto(
			BigDecimal.valueOf(10000),
			null,
			BigDecimal.valueOf(500)
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
	@DisplayName("주문 폼 정보 가져오기 - 장바구니 구매(회원)")
	void getOrderInformationByCartMemberTest() throws Exception {

		MemberIdContext.setMemberId(1L);
		Long memberId = MemberIdContext.getMemberId();

		when(orderService.getMemberCartOrderInformation(memberId)).thenReturn(
			memberOrderResponse
		);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/cart/{cart-id}", "cartId1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.customer.name").isNotEmpty());

	}

	@Test
	@DisplayName("주문 폼 정보 가져오기 - 장바구니 구매(비회원)")
	void getOrderInformationByCartCustomTest() throws Exception {

		String cartId = "cartId1";
		when(orderService.getCustomerCartOrderInformation(cartId)).thenReturn(
			customerOrderResponse
		);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/cart/{cart-id}", cartId))
			.andExpect(status().isOk());

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

	@Test
	@DisplayName("최종 결제 전 검증 테스트")
	void verifyOrderTest() throws Exception {
		when(orderService.verifyOrderInformation(any(PaymentRequest.class)))
			.thenReturn(ResponseMessage.builder()
				.statusCode(200)
				.message("토스 페이먼츠 검증 완료")
				.build());

		mockMvc.perform(MockMvcRequestBuilders.post("/api/orders/verify")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(paymentRequest)))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.message")
				.value("토스 페이먼츠 검증 완료"));
	}

	@Test
	@DisplayName("회원 주문 목록 조회")
	void getOrderListMemberTest() throws Exception {

		MemberIdContext.setMemberId(1L);
		Optional<Long> memberId = Optional.ofNullable(MemberIdContext.getMemberId());

		when(orderService.getOrderList(
			anyBoolean(),
			any(Pageable.class),
			any(OrderListPeriodRequest.class),
			eq(memberId)))
			.thenReturn(orderListPage);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/orders")
				.param("includeOrdersInPendingStatus", "true")
				.param("page", "0")
				.param("size", "10")
				.contentType("application/json"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content.length()").value(2));

	}

	@Test
	@DisplayName("비회원 주문 목록 조회 테스트")
	void getOrderListCustomTest() throws Exception {

		Optional<Long> memberId = Optional.of(1L);

		when(orderService.getNonMemberOrderList(
			anyBoolean(),
			any(Pageable.class),
			any(OrderListPeriodRequest.class),
			eq(memberId)))
			.thenReturn(orderListPage);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/orders//non-member/{customer-id}", 1L)
				.param("includeOrdersInPendingStatus", "true")
				.param("page", "0")
				.param("size", "10")
				.contentType("application/json"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content.length()").value(2));

	}

	@Test
	@DisplayName("주문 환불/취소 목록 조회 테스트")
	void getCancelOrderListTest() throws Exception {

		MemberIdContext.setMemberId(1L);
		Optional<Long> memberId = Optional.ofNullable(MemberIdContext.getMemberId());

		when(orderService.getCancelledOrderList(
			any(Pageable.class),
			any(OrderListPeriodRequest.class),
			eq(memberId)
		))
			.thenReturn(orderListPage);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/cancel")
				.param("page", "0")
				.param("size", "10")
				.contentType("application/json"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content.length()").value(2));

	}

	@Test
	@DisplayName("회원 주문 상세 조회 테스트")
	void getOrderDetailMemberTest() throws Exception {
		MemberIdContext.setMemberId(1L);
		Optional<Long> memberId = Optional.ofNullable(MemberIdContext.getMemberId());

		when(orderService.getOrderDetail(eq(memberId), anyLong(), any(Pageable.class)))
			.thenReturn(orderDetailResponse);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/details/{order-id}", 1L)
				.param("page", "0")
				.param("size", "10")
				.contentType("application/json"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.shipping").isNotEmpty());
	}

	@Test
	@DisplayName("비회원 주문 상세 조회 테스트")
	void getOrderDetailCustomeTest() throws Exception {

		Optional<Long> customerId = Optional.of(1L);

		when(orderService.getNonMemberOrderDetail(eq(customerId), anyLong(), any(Pageable.class)))
			.thenReturn(orderDetailResponse);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/orders//{order-id}/non-member/{customer-id}", 1L, 1L)
				.param("order-id", "1")
				.param("customer-id", "1")
				.param("page", "0")
				.param("size", "10")
				.contentType("application/json"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.shipping").isNotEmpty());
	}

	@Test
	@DisplayName("주문 취소 정보 불러오기 테스트")
	void getOrderCancelTest() throws Exception {
		MemberIdContext.setMemberId(1L);
		Optional<Long> memberId = Optional.ofNullable(MemberIdContext.getMemberId());

		when(orderService.getOrderCancel(eq(memberId), anyLong()))
			.thenReturn(orderCancelDto);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/{order-id}/cancel", 1L)
				.param("order-id", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.paymentPrice").value(BigDecimal.valueOf(10000)));
	}

	@Test
	@DisplayName("주문 취소 처리 테스트 - 회원")
	void doOrderCancelMemberTest() throws Exception {

		Long orderId = 1L;
		MemberIdContext.setMemberId(1L);
		Optional<Long> memberId = Optional.ofNullable(MemberIdContext.getMemberId());

		OrderCancelRequest orderCancelRequest = new OrderCancelRequest("취소 사유", -1L);
		ResponseMessage mockResponse = new ResponseMessage(200, "Success");
		when(orderService.doOrderCancel(eq(memberId.get()), eq(orderId), any(OrderCancelRequest.class)))
			.thenReturn(mockResponse);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/orders/{order-id}/cancel", orderId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(orderCancelRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("Success"))
			.andExpect(jsonPath("$.statusCode").value(200));
	}

	@Test
	@DisplayName("주문 취소 처리 테스트 - 비회원")
	void doOrderCancelNonMemberTest() throws Exception {

		Long orderId = 1L;
		OrderCancelRequest orderCancelRequest = new OrderCancelRequest("취소 사유", 1L);

		ResponseMessage mockResponse = new ResponseMessage(200, "Success");
		when(orderService.doOrderCancel(anyLong(), eq(orderId), any(OrderCancelRequest.class)))
			.thenReturn(mockResponse);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/orders/{order-id}/cancel", orderId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(orderCancelRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("Success"))
			.andExpect(jsonPath("$.statusCode").value(200));

		verify(orderService, times(1)).doOrderCancel(anyLong(), eq(orderId), any(OrderCancelRequest.class));
	}
}
