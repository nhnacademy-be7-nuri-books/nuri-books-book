package shop.nuribooks.books.order.order.controller;

import static shop.nuribooks.books.cart.entity.RedisCartKey.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.cart.dto.response.CartBookResponse;
import shop.nuribooks.books.common.message.ResponseMessage;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;
import shop.nuribooks.books.order.order.dto.OrderInformationResponse;
import shop.nuribooks.books.order.order.dto.OrderTempRegisterRequest;
import shop.nuribooks.books.order.order.dto.OrderTempRegisterResponse;
import shop.nuribooks.books.order.order.service.OrderService;
import shop.nuribooks.books.payment.payment.dto.PaymentRequest;
import shop.nuribooks.books.payment.payment.dto.PaymentSuccessRequest;

/**
 * 주문 관련 Controller
 *
 * @author : nuri
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

	private final OrderService orderService;

	/**
	 * 주문 폼 정보 가져오기 - 바로 구매 ( 단일 상품)
	 *
	 * @param bookId 상품 아이디
	 * @param quantity 상품 갯수
	 * @return OrderInformationResponse
	 */
	@GetMapping("/{book-id}")
	public ResponseEntity<OrderInformationResponse> getOrderInformation(
		@PathVariable("book-id") Long bookId, @RequestParam Integer quantity){

		Optional<Long> userId = Optional.ofNullable(MemberIdContext.getMemberId());

		OrderInformationResponse result = userId
			// 회원 주문에 필요한 정보 가져오기
			.map(id -> orderService.getMemberOrderInformation(id, bookId, quantity))
			// 비회원 주문에 필요한 정보 가져오기
			.orElseGet(() -> orderService.getCustomerOrderInformation(bookId, quantity));

		AtomicReference<OrderInformationResponse> orderInformationResponse = new AtomicReference<>();

		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

	// 장바구니 주문 정보 가져오기
	@GetMapping("/cart/{cart-id}")
	public ResponseEntity<OrderInformationResponse> getCartOrderInformation(@PathVariable("cart-id") String requestCartId){

		OrderInformationResponse result;
		Long memberId = MemberIdContext.getMemberId();
		String cartId;
		if (Objects.nonNull(memberId)) {
			result = orderService.getMemberCartOrderInformation(memberId);
		} else {
			cartId = CUSTOMER_KEY.withSuffix(requestCartId);
			result = orderService.getCustomerCartOrderInformation(cartId);
		}

		return ResponseEntity.status(HttpStatus.OK).body(result);
	}


	/**
	 * 결제 전 임시 주문 저장
	 *
	 * @param orderTempRegisterRequest 임시 주문 등록 요청
	 * @return 임시 주문 등록 응답
	 */
	@Operation(summary = "회원/비회원 주문 임시 저장", description = "회원 또는 비회원의 주문을 임시 저장합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "주문 임시 저장 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "404", description = "사용자 또는 상품을 찾을 수 없음")
	})
	@PostMapping
	public ResponseEntity<OrderTempRegisterResponse> registerTempOrder(
		@Valid @RequestBody OrderTempRegisterRequest orderTempRegisterRequest) {

		Optional<Long> userId = Optional.ofNullable(MemberIdContext.getMemberId());

		OrderTempRegisterResponse result = userId
			// 회원 주문
			.map(id -> orderService.registerTempOrderForMember(id, orderTempRegisterRequest))
			// 비회원 주문
			.orElseGet(() -> orderService.registerTempOrderForCustomer(orderTempRegisterRequest));

		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}

	/**
	 * 최종 결제 전 검증
	 *
	 * @param paymentRequest 토스 페이먼츠 요청 값
	 * @return 응답 메시지
	 */
	@PostMapping("/verify")
	public ResponseEntity<ResponseMessage> verifyOrderInformation(PaymentRequest paymentRequest){

		ResponseMessage responseMessage = orderService.verifyOrderInformation(paymentRequest);

		return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
	}

}
