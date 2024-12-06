package shop.nuribooks.books.book.coupon.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.book.coupon.dto.CouponPolicyRequest;
import shop.nuribooks.books.book.coupon.dto.CouponPolicyResponse;
import shop.nuribooks.books.book.coupon.enums.DiscountType;
import shop.nuribooks.books.book.coupon.service.CouponPolicyService;

@WebMvcTest(CouponPolicyController.class)
class CouponPolicyControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CouponPolicyService couponPolicyService;

	@Autowired
	private ObjectMapper objectMapper;

	private CouponPolicyRequest couponPolicyRequest;
	private CouponPolicyResponse couponPolicyResponse;

	@BeforeEach
	void setUp() {
		couponPolicyRequest = new CouponPolicyRequest("테스트 쿠폰정책", DiscountType.FIXED, BigDecimal.valueOf(10000), null,
			5000);
		couponPolicyResponse = new CouponPolicyResponse(1L, "테스트 쿠폰정책", DiscountType.FIXED, BigDecimal.valueOf(10000),
			null, 5000);

	}

	@DisplayName("모든 쿠폰 정책 조회")
	@Test
	void getCouponPolicies() throws Exception {

		when(couponPolicyService.getCouponPolicies(any())).thenReturn(
			new PageImpl<>(List.of(this.couponPolicyResponse),
				PageRequest.of(0, 10), 1));
		mockMvc.perform(get("/api/coupon-policies"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.*", Matchers.hasSize(11)));
	}

	@DisplayName("쿠폰 정책 등록")
	@Test
	void registerCouponPolicy() throws Exception {
		when(couponPolicyService.registerCouponPolicy(any())).thenReturn(couponPolicyRequest.toEntity());
		mockMvc.perform(post("/api/coupon-policies")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(couponPolicyRequest)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.*", Matchers.hasSize(2)));
	}

	@DisplayName("쿠폰 정책 조회")
	@Test
	void getCouponPolicy() throws Exception {
		Long couponPolicyId = 1L;

		when(couponPolicyService.getCouponPolicy(any())).thenReturn(couponPolicyResponse);
		mockMvc.perform(get("/api/coupon-policies/{coupon-policy-id}", couponPolicyId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(couponPolicyRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.*", Matchers.hasSize(6)));
	}

	@DisplayName("쿠폰 정책 업데이트")
	@Test
	void updateCouponPolicy() throws Exception {
		Long couponPolicyId = 1L;

		when(couponPolicyService.updateCouponPolicy(anyLong(), any())).thenReturn(couponPolicyRequest.toEntity());
		mockMvc.perform(put("/api/coupon-policies/{coupon-policy-id}", couponPolicyId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(couponPolicyRequest)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.*", Matchers.hasSize(2)));

		verify(couponPolicyService).updateCouponPolicy(couponPolicyId, couponPolicyRequest);
	}

	@DisplayName("쿠폰 정책 삭제")
	@Test
	void deleteCouponPolicy() throws Exception {
		Long couponPolicyId = 1L;

		mockMvc.perform(delete("/api/coupon-policies/{coupon-policy-id}", couponPolicyId))
			.andExpect(status().isNoContent())
			.andExpect(jsonPath("$.*", Matchers.hasSize(2)));

		verify(couponPolicyService).deleteCouponPolicy(couponPolicyId);
	}
}