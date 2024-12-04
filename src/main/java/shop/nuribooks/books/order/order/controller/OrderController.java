package shop.nuribooks.books.order.order.controller;

import static shop.nuribooks.books.cart.entity.RedisCartKey.*;

import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.common.annotation.HasRole;
import shop.nuribooks.books.common.message.ResponseMessage;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;
import shop.nuribooks.books.member.member.entity.AuthorityType;
import shop.nuribooks.books.order.order.dto.OrderCancelDto;
import shop.nuribooks.books.order.order.dto.request.OrderCancelRequest;
import shop.nuribooks.books.order.order.dto.request.OrderListPeriodRequest;
import shop.nuribooks.books.order.order.dto.request.OrderRegisterRequest;
import shop.nuribooks.books.order.order.dto.response.OrderInformationResponse;
import shop.nuribooks.books.order.order.dto.response.OrderListResponse;
import shop.nuribooks.books.order.order.dto.response.OrderRegisterResponse;
import shop.nuribooks.books.order.order.service.OrderService;
import shop.nuribooks.books.order.orderdetail.dto.OrderDetailResponse;
import shop.nuribooks.books.payment.payment.dto.PaymentRequest;

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
	@Operation(summary = "단일 상품 주문 폼 정보", description = "단일 상품에 대해 주문 정보를 가져옵니다.")
	@ApiResponse(responseCode = "200", description = "주문 정보 조회 성공")
	@ApiResponse(responseCode = "400", description = "잘못된 요청")
	@GetMapping("/{book-id}")
	public ResponseEntity<OrderInformationResponse> getOrderInformation(
		@PathVariable("book-id") Long bookId, @RequestParam Integer quantity) {

		Optional<Long> memberId = Optional.ofNullable(MemberIdContext.getMemberId());

		OrderInformationResponse result = memberId
			// 회원 주문에 필요한 정보 가져오기
			.map(id -> orderService.getMemberOrderInformation(id, bookId, quantity))
			// 비회원 주문에 필요한 정보 가져오기
			.orElseGet(() -> orderService.getCustomerOrderInformation(bookId, quantity));

		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

	/**
	 * 장바구니 주문 폼 정보 가져오기
	 *
	 * @param requestCartId 장바구니 아이디
	 * @return OrderInformationResponse
	 */
	@Operation(summary = "장바구니 주문 폼 정보", description = "장바구니에 담긴 상품들에 대해 주문 정보를 가져옵니다.")
	@ApiResponse(responseCode = "200", description = "주문 정보 조회 성공")
	@ApiResponse(responseCode = "400", description = "잘못된 요청")
	@GetMapping("/cart/{cart-id}")
	public ResponseEntity<OrderInformationResponse> getCartOrderInformation(
		@PathVariable("cart-id") String requestCartId) {

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
	@ApiResponse(responseCode = "201", description = "주문 임시 저장 성공")
	@ApiResponse(responseCode = "400", description = "잘못된 요청")
	@ApiResponse(responseCode = "404", description = "사용자 또는 상품을 찾을 수 없음")
	@PostMapping
	public ResponseEntity<OrderRegisterResponse> registerTempOrder(
		@Valid @RequestBody OrderRegisterRequest orderTempRegisterRequest) {

		Optional<Long> memberId = Optional.ofNullable(MemberIdContext.getMemberId());

		OrderRegisterResponse result = memberId
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
	@Operation(summary = "최종 결제 전 검증", description = "결제 전 주문 정보를 검증합니다.")
	@ApiResponse(responseCode = "200", description = "검증 성공")
	@ApiResponse(responseCode = "400", description = "잘못된 요청")
	@PostMapping("/verify")
	public ResponseEntity<ResponseMessage> verifyOrderInformation(@RequestBody PaymentRequest paymentRequest) {

		ResponseMessage responseMessage = orderService.verifyOrderInformation(paymentRequest);

		return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
	}

	/**
	 * 주문 목록 조회
	 *
	 * @return 주문 목록
	 */
	@HasRole(role = AuthorityType.MEMBER)
	@GetMapping
	@Operation(summary = "주문 목록 조회", description = "회원의 주문 목록을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "성공")
	@ApiResponse(responseCode = "400", description = "잘못된 요청")
	public ResponseEntity<Page<OrderListResponse>> getOrderList(
		OrderListPeriodRequest orderListPeriodRequest,
		boolean includeOrdersInPendingStatus,
		Pageable pageable) {

		Optional<Long> memberId = Optional.ofNullable(MemberIdContext.getMemberId());

		Page<OrderListResponse> result = orderService.getOrderList(includeOrdersInPendingStatus, pageable,
			orderListPeriodRequest, memberId);

		log.debug("회원 주문 목록 조회 성공");

		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

	@Operation(summary = "비회원 주문 목록 조회", description = "회원의 주문 목록을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "성공")
	@ApiResponse(responseCode = "400", description = "잘못된 요청")
	@GetMapping("/non-member/{customer-id}")
	public ResponseEntity<Page<OrderListResponse>> getNonMemberOrderList(
		OrderListPeriodRequest orderListPeriodRequest,
		boolean includeOrdersInPendingStatus,
		Pageable pageable,
		@PathVariable("customer-id") Long customerId) {
		Page<OrderListResponse> result = orderService.getNonMemberOrderList(includeOrdersInPendingStatus, pageable,
			orderListPeriodRequest, Optional.of(customerId));

		log.debug("비회원 주문 목록 조회 성공");

		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

	/**
	 * 주문 환불/취소 목록 조회
	 *
	 * @return 주문 목록
	 */
	@Operation(summary = "주문 취소/환불 목록 조회", description = "회원의 취소된 주문 목록을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "성공")
	@ApiResponse(responseCode = "400", description = "잘못된 요청")
	@ApiResponse(responseCode = "500", description = "서버 오류")
	@HasRole(role = AuthorityType.MEMBER)
	@GetMapping("/cancel")
	public ResponseEntity<Page<OrderListResponse>> getCancelledOrderList(
		OrderListPeriodRequest orderListPeriodRequest,
		Pageable pageable) {

		Optional<Long> memberId = Optional.ofNullable(MemberIdContext.getMemberId());

		Page<OrderListResponse> result = orderService.getCancelledOrderList(pageable,
			orderListPeriodRequest, memberId);

		log.debug("주문 취소/환불 조회 성공");

		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

	/**
	 * 주문 상세 조회
	 *
	 * @param orderId 주문 아이디
	 * @param pageable 페이징
	 * @return 주문 상세 정보
	 */
	@Operation(summary = "주문 상세 조회", description = "특정 주문의 상세 정보를 조회합니다.")
	@ApiResponse(responseCode = "200", description = "성공")
	@ApiResponse(responseCode = "400", description = "잘못된 요청")
	@ApiResponse(responseCode = "500", description = "서버 오류")
	@GetMapping("/details/{order-id}")
	public ResponseEntity<OrderDetailResponse> getOrderDetail(
		@PathVariable("order-id") Long orderId,
		Pageable pageable
	) {
		Optional<Long> memberId = Optional.ofNullable(MemberIdContext.getMemberId());

		log.debug("주문 상세 조회 성공");

		OrderDetailResponse result = orderService.getOrderDetail(memberId, orderId, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

	@Operation(summary = "비회원 주문 상세 조회", description = "특정 주문의 상세 정보를 조회합니다.")
	@ApiResponse(responseCode = "200", description = "성공")
	@ApiResponse(responseCode = "400", description = "잘못된 요청")
	@ApiResponse(responseCode = "500", description = "서버 오류")
	@GetMapping("/{order-id}/non-member/{customer-id}")
	public ResponseEntity<OrderDetailResponse> getNonMemberOrderDetail(
		@PathVariable("order-id") Long orderId,
		@PathVariable("customer-id") Long customerId,
		Pageable pageable
	) {
		OrderDetailResponse result = orderService.getNonMemberOrderDetail(Optional.of(customerId), orderId, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

	/**
	 * 주문 취소 폼
	 *
	 * @param orderId 주문 아이디
	 * @return 주문 상세 정보
	 */
	@Operation(
		summary = "주문 취소 폼",
		description = "주문 아이디에 해당하는 주문의 취소 정보를 가져옵니다."
	)
	@ApiResponse(responseCode = "200", description = "주문 취소 폼 정보 조회 성공")
	@ApiResponse(responseCode = "400", description = "잘못된 요청")
	@ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
	@GetMapping("/{order-id}/cancel")
	public ResponseEntity<OrderCancelDto> getOrderCancel(
		@PathVariable("order-id") Long orderId
	) {
		Optional<Long> memberId = Optional.ofNullable(MemberIdContext.getMemberId());
		OrderCancelDto result = orderService.getOrderCancel(memberId, orderId);

		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

	/**
	 * 주문 취소
	 *
	 * @param orderId 주문 아이디
	 * @return 주문 상세 정보
	 */
	@Operation(
		summary = "주문 취소",
		description = "주문 아이디에 해당하는 주문을 취소합니다."
	)
	@ApiResponse(responseCode = "200", description = "주문 취소 성공")
	@ApiResponse(responseCode = "400", description = "잘못된 요청")
	@ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
	@ApiResponse(responseCode = "401", description = "권한 없음")
	@PostMapping("/{order-id}/cancel")
	public ResponseEntity<ResponseMessage> doOrderCancel(
		@PathVariable("order-id") Long orderId,
		@RequestBody OrderCancelRequest orderCancelRequest) {

		Long customerId = orderCancelRequest.customerId();
		// 비회원 일 경우
		if (customerId > -1) {
			ResponseMessage result = orderService.doOrderCancel(customerId, orderId, orderCancelRequest);
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} else {
			// 회원일 경우
			Optional<Long> member = Optional.ofNullable(MemberIdContext.getMemberId());

			ResponseMessage result = orderService.doOrderCancel(member.get(), orderId, orderCancelRequest);
			return ResponseEntity.status(HttpStatus.OK).body(result);
		}

	}

}
