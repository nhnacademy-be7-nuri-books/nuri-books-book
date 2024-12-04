package shop.nuribooks.books.order.shipping.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.order.shipping.dto.ShippingPolicyRequest;
import shop.nuribooks.books.order.shipping.dto.ShippingPolicyResponse;
import shop.nuribooks.books.order.shipping.entity.ShippingPolicy;
import shop.nuribooks.books.order.shipping.service.ShippingPolicyService;

@WebMvcTest(ShippingPolicyAdminController.class)
class ShippingPolicyControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	ShippingPolicyService shippingPolicyService;

	@Test
	void testGetShippingPolicies() throws Exception {
		// Given
		Pageable pageable = PageRequest.of(0, 3);
		ShippingPolicyResponse response = new ShippingPolicyResponse(1L, 1000, null, BigDecimal.valueOf(10000));
		when(shippingPolicyService.getShippingPolicyResponses(any()))
			.thenReturn(new PageImpl<>(List.of(response), pageable, 1));

		// When & Then
		mockMvc.perform(get("/api/shipping-policies"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.*", Matchers.hasSize(11)));
	}

	@Test
	void testRegisterShippingPolicy() throws Exception {
		// Given
		ShippingPolicyRequest request = new ShippingPolicyRequest(1000, null, BigDecimal.valueOf(10000));
		when(shippingPolicyService.registerShippingPolicy(any(ShippingPolicyRequest.class))).thenReturn(
			ShippingPolicy.builder().build());

		// When & Then
		mockMvc.perform(post("/api/shipping-policies")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.message", Matchers.containsString("성공")));
	}

	@Test
	void testUpdateShippingPolicy() throws Exception {
		// Given
		ShippingPolicyRequest request = new ShippingPolicyRequest(1000, null, BigDecimal.valueOf(10000));
		when(shippingPolicyService.updateShippingPolicy(anyLong(), any(ShippingPolicyRequest.class))).thenReturn(
			ShippingPolicy.builder().build());

		// When & Then
		mockMvc.perform(put("/api/shipping-policies/{shipping_policy_id}", 1)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", Matchers.containsString("성공")));
	}

	@Test
	void testExpireShippingPolicy() throws Exception {
		// Given
		Mockito.doNothing().when(shippingPolicyService).expireShippingPolicy(anyLong());

		// When & Then
		mockMvc.perform(put("/api/shipping-policies/{shipping_policy_id}/expire", 1)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", Matchers.containsString("성공")));
	}
}
