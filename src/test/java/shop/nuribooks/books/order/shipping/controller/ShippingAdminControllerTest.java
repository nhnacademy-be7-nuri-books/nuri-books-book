package shop.nuribooks.books.order.shipping.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.order.shipping.dto.ShippingResponse;
import shop.nuribooks.books.order.shipping.service.ShippingAdminService;

@WebMvcTest(ShippingAdminController.class)
class ShippingAdminControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ShippingAdminService shippingAdminService;

	@Test
	void testGetShippingResponses() throws Exception {
		// Given
		Pageable pageable = PageRequest.of(0, 10);
		ShippingResponse response = new ShippingResponse(1L, 1L, TestUtils.createShippingPolicy(), "받는", "adress",
			"detail", "12345", "01012345678", "sender", "01029384723", null, "qwerasdf", null);
		when(shippingAdminService.getShippingResponses(any(Pageable.class)))
			.thenReturn(new PageImpl<>(List.of(response), pageable, 1));

		// When & Then
		mockMvc.perform(get("/api/shipping")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.*", Matchers.hasSize(11)));
	}

	@Test
	void testGetShippingResponse() throws Exception {
		// Given
		ShippingResponse response = new ShippingResponse(1L, 1L, TestUtils.createShippingPolicy(), "받는", "adress",
			"detail", "12345", "01012345678", "sender", "01029384723", null, "qwerasdf", null);
		when(shippingAdminService.getShippingResponse(anyLong())).thenReturn(response);

		// When & Then
		mockMvc.perform(get("/api/shipping/{id}", 1)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", Matchers.is(1)));
	}

	@Test
	void testUpdateDeliveryStatus() throws Exception {
		// Given
		Mockito.doNothing().when(shippingAdminService).updateDeliveryStatus(anyLong());

		// When & Then
		mockMvc.perform(put("/api/shipping/{id}", 1)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", Matchers.containsString("배송이 시작되었습니다.")));
	}

	@Test
	void testCompleteDelivery() throws Exception {
		// Given
		Mockito.doNothing().when(shippingAdminService).completeDelivery(anyLong());

		// When & Then
		mockMvc.perform(put("/api/shipping/delivery-complete/{id}", 1)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", Matchers.containsString("배송 완료 처리!")));
	}
}