package shop.nuribooks.books.cart.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.cart.dto.request.CartAddRequest;
import shop.nuribooks.books.cart.dto.request.CartLoadRequest;
import shop.nuribooks.books.cart.dto.response.CartBookResponse;
import shop.nuribooks.books.cart.service.CartService;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;

@RequiredArgsConstructor
@RestController
public class CartController {
	private static final String MEMBER_CART_KEY = "member:";
	private static final String CUSTOMER_CART_KEY = "customer:";

	private final CartService cartService;

	@PostMapping("/api/cart")
	public ResponseEntity addToCart(@RequestBody @Valid CartAddRequest cartAddRequest) {
		String cartId;
		Long memberId = MemberIdContext.getMemberId();
		// 비회원인 경우
		if (Objects.isNull(memberId)) {
			cartId = CUSTOMER_CART_KEY + cartAddRequest.cartId();
			cartService.addCustomerCart(cartId, cartAddRequest);
		} else {
			cartId = MEMBER_CART_KEY + memberId;
			cartService.addMemberCart(cartId, cartAddRequest);
		}
		return ResponseEntity.ok().build();
	}

	@GetMapping("/api/cart/{cart-id}")
	public ResponseEntity<List<CartBookResponse>> getCartList(@PathVariable("cart-id") String requestCartId) {
		Long memberId = MemberIdContext.getMemberId();
		String cartId;
		if (Objects.isNull(memberId)) {
			cartId = CUSTOMER_CART_KEY + requestCartId;
		} else {
			cartId = MEMBER_CART_KEY + requestCartId;
		}
		List<CartBookResponse> cartResponseList = cartService.getCart(cartId);
		return ResponseEntity.ok().body(cartResponseList);
	}

	@PostMapping("/api/cart/load-to-redis")
	public ResponseEntity<Void> loadCartToRedis(@RequestBody CartLoadRequest request) {
		cartService.loadCart(request);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/api/cart/{cart-id}/book/{book-id}")
	public ResponseEntity removeCartItem(@PathVariable("cart-id") String requestCartId,
		@PathVariable("book-id") Long bookId) {
		Long memberId = MemberIdContext.getMemberId();
		String cartId;
		if (Objects.isNull(memberId)) {
			cartId = CUSTOMER_CART_KEY + requestCartId;
		} else {
			cartId = MEMBER_CART_KEY + requestCartId;
		}
		cartService.removeCartItem(cartId, bookId);
		return ResponseEntity.noContent().build();
	}

}
