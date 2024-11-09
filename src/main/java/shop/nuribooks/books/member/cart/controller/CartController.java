package shop.nuribooks.books.member.cart.controller;

import static org.springframework.http.HttpStatus.*;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.common.message.ResponseMessage;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;
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
	 * ThreadLocal에서 회원의 PK id를, PathVariable로 도서의 PK id와 수량을 받음 <br>
	 * 생성 후 해당 도서의 상세 정보들을 CartAddResponse에 담아서 반환
	 */
	@Operation(summary = "장바구니 생성", description = "회원과 도서의 id로 장바구니를 생성합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "장바구니 생성 성공"),
		@ApiResponse(responseCode = "400", description = "도서 수량이 유효하지 않음"),
		@ApiResponse(responseCode = "404", description = "회원 혹은 도서가 존재하지 않음")
	})
	@PostMapping("/{bookId}/{quantity}")
	public ResponseEntity<CartAddResponse> addToCart(
		@PathVariable Long bookId, @PathVariable int quantity) {

		Long memberId = MemberIdContext.getMemberId();

		CartAddResponse response = cartService.addToCart(memberId, bookId, quantity);

		return ResponseEntity.status(CREATED).body(response);
	}

	/**
	 * 장바구니 조회 <br>
	 * ThreadLocal에서 회원의 PK id를 가져와서 장바구니 조회
	 */
	@Operation(summary = "장바구니 조회", description = "회원 PK id로 장바구니를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "장바구니 조회 성공")
	})
	@GetMapping("/me")
	public ResponseEntity<List<CartListResponse>> getCartList() {

		Long memberId = MemberIdContext.getMemberId();
		List<CartListResponse> response = cartService.getCartList(memberId);

		return ResponseEntity.status(OK).body(response);
	}

	/**
	 * 장바구니 삭제 <br>
	 * ThreadLocal에서 회원의 PK id를 가져와서 장바구니 삭제
	 */
	@Operation(summary = "장바구니 삭제", description = "회원 PK id로 장바구니를 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "장바구니 삭제 성공")
	})
	@DeleteMapping("/me/{bookId}")
	public ResponseEntity<ResponseMessage> cartDelete(@PathVariable Long bookId) {

		Long memberId = MemberIdContext.getMemberId();
		cartService.deleteCart(memberId, bookId);

		return ResponseEntity.status(OK).body(new ResponseMessage(OK.value(),
			"장바구니에서 상품이 삭제되었습니다."));
	}
}

