package shop.nuribooks.books.order.order.controller;

import java.util.Objects;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;
import shop.nuribooks.books.order.order.dto.OrderTempRegisterRequest;
import shop.nuribooks.books.order.order.dto.OrderTempRegisterResponse;
import shop.nuribooks.books.order.order.service.OrderService;

/**
 * 주문 관련 Controller
 *
 * @author : nuri
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderService orderService;

	/**
	 * 결제 전 임시 주문 저장
	 *
	 * @param request 요청 헤더 정보를 얻어오기 위한 HttpServletRequest
	 * @param orderTempRegisterRequest 임시 주문 등록 요청
	 * @return 임시 주문 등록 응답
	 */
	@Operation(summary = "회원/비회원 주문 임시 저장", description = "회원 또는 비회원의 주문을 임시 저장합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "주문 임시 저장 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "404", description = "사용자 또는 상품을 찾을 수 없음")
	})
	@PostMapping("")
	public ResponseEntity<OrderTempRegisterResponse> registerTempOrder(
		HttpServletRequest request,
		@Valid @RequestBody OrderTempRegisterRequest orderTempRegisterRequest) {

		Optional<Long> userId = Optional.ofNullable(MemberIdContext.getMemberId());

		OrderTempRegisterResponse result = userId
			// 회원 주문
			.map(id -> orderService.registerTempOrderForMember(id, orderTempRegisterRequest))
			// 비회원 주문
			.orElseGet(() -> orderService.registerTempOrderForCustomer(orderTempRegisterRequest));

		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}

}
