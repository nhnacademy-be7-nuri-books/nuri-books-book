package shop.nuribooks.books.member.cart.controller;

import static org.springframework.http.HttpStatus.*;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.member.cart.dto.request.CartAddRequest;
import shop.nuribooks.books.member.cart.dto.response.CartAddResponse;
import shop.nuribooks.books.member.cart.dto.response.CartListResponse;
import shop.nuribooks.books.member.cart.service.CartService;

/**
 * @author Jprotection
 */
@RestController
@RequestMapping("/api/member/cart")
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;

	/**
	 * 장바구니 생성 <br>
	 * 회원과 도서의 PK id와 수량을 CartAddRequest에 담아 요청 <br>
	 * 생성 후 해당 도서의 상세 정보들을 CartAddResponse에 담아서 반환
	 */
	@Operation(summary = "장바구니 생성", description = "회원과 도서의 id로 장바구니를 생성합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "장바구니 생성 성공"),
		@ApiResponse(responseCode = "404", description = "회원 혹은 도서가 존재하지 않음")
	})
	@PostMapping
	public ResponseEntity<CartAddResponse> addToCart(@RequestBody CartAddRequest request) {
		CartAddResponse response = cartService.addToCart(request);

		return ResponseEntity.status(CREATED).body(response);
	}

	@GetMapping("/{memberId}")
	public ResponseEntity<List<CartListResponse>> getCartList(@PathVariable Long memberId) {
		List<CartListResponse> response = cartService.getCartList(memberId);

		return ResponseEntity.status(OK).body(response);
	}
}

