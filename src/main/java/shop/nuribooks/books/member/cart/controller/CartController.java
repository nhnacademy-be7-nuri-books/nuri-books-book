package shop.nuribooks.books.member.cart.controller;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.member.cart.service.CartService;

/**
 * @author Jprotection
 */
@RestController
@RequestMapping("/api/member/cart")
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;

	@PostMapping("/{memberId}/{bookId}")
	public ResponseEntity<Void> addToCart(@PathVariable Long memberId, @PathVariable Long bookId) {
		cartService.addToCart(memberId, bookId);

		return ResponseEntity.status(CREATED).build();
	}
}
