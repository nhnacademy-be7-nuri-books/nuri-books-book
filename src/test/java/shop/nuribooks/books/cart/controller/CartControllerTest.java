package shop.nuribooks.books.cart.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.cart.dto.request.CartAddRequest;
import shop.nuribooks.books.cart.dto.request.CartLoadRequest;
import shop.nuribooks.books.cart.dto.response.CartBookResponse;
import shop.nuribooks.books.cart.service.CartService;

@WebMvcTest(CartController.class)
class CartControllerTest {

	@Autowired
	ObjectMapper objectMapper;
	@MockBean
	private CartService cartService;
	@Autowired
	private MockMvc mockMvc;

	@DisplayName("비회원 장바구니에 도서를 담는다.")
	@Test
	void addCustomerCart() throws Exception {
		// given
		CartAddRequest request = new CartAddRequest("cartId", 1L, 1);

		// when then
		mockMvc.perform(post("/api/cart")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk());
	}

	@DisplayName("비회원 장바구니 리스트를 가져온다")
	@Test
	void getCustomerCartList() throws Exception {
		// given
		CartBookResponse cartBookResponse1 = mock(CartBookResponse.class);
		CartBookResponse cartBookResponse2 = mock(CartBookResponse.class);

		when(cartService.getCart(anyString())).thenReturn(List.of(cartBookResponse1, cartBookResponse2));

		String cartId = "cartId";
		// when then
		mockMvc.perform(get("/api/cart/{cart-id}", cartId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.*", hasSize(2)));
	}

	@DisplayName("비회원 장바구니에서 특정 아이템을 삭제한다")
	@Test
	void removeCustomerCartItem() throws Exception {
		// given
		String cartId = "cartId";
		Long bookId = 1L;

		// when
		mockMvc.perform(delete("/api/cart/{cart-id}/book/{bookId}", cartId, bookId))
			.andExpect(status().isNoContent());

		// then
		verify(cartService).removeCartItem(anyString(), anyLong());
	}

	@DisplayName("회원 장바구니를 로드한다.")
	@Test
	void loadCartToRedis() throws Exception {
		// given
		CartLoadRequest cartLoadRequest = new CartLoadRequest(1L);

		// when
		mockMvc.perform(post("/api/cart/load-to-redis")
				.content(objectMapper.writeValueAsString(cartLoadRequest))
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk());

		// then
		verify(cartService).loadCart(any());
	}

}
