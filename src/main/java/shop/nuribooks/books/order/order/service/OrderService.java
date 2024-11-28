package shop.nuribooks.books.order.order.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;
import shop.nuribooks.books.common.message.ResponseMessage;
import shop.nuribooks.books.order.order.dto.OrderCancelDto;
import shop.nuribooks.books.order.order.dto.request.OrderListPeriodRequest;
import shop.nuribooks.books.order.order.dto.request.OrderRegisterRequest;
import shop.nuribooks.books.order.order.dto.response.OrderInformationResponse;
import shop.nuribooks.books.order.order.dto.response.OrderListResponse;
import shop.nuribooks.books.order.order.dto.response.OrderRegisterResponse;
import shop.nuribooks.books.order.orderdetail.dto.OrderDetailResponse;
import shop.nuribooks.books.payment.payment.dto.PaymentRequest;

/**
 * 주문 서비스 인터페이스
 *
 * @author nuri
 */
public interface OrderService {

	/**
	 * 주문 폼 정보 가져오기 - 바로 주문(회원)
	 *
	 * @param memberId 사용자 아이디
	 * @param bookId 상품 아이디
	 * @param quantity 상품 갯수
	 * @return OrderInformationResponse
	 */
	OrderInformationResponse getMemberOrderInformation(Long memberId, Long bookId, int quantity);

	/**
	 * 주문 폼 정보 가져오기 - 바로 주문(비회원)
	 *
	 * @param bookId 상품 아이디
	 * @param quantity 상품 갯수
	 * @return OrderInformationResponse
	 */
	OrderInformationResponse getCustomerOrderInformation(Long bookId, int quantity);

	OrderInformationResponse getMemberCartOrderInformation(Long memberId);

	OrderInformationResponse getCustomerCartOrderInformation(String cartId);

	/**
	 * 회원 주문 임시 등록
	 *
	 * @param memberId UserId pk
	 * @param orderTempRegisterRequest 주문 임시 등록 request
	 * @return 주문 임시 등록 response
	 */
	OrderRegisterResponse registerTempOrderForMember(Long memberId,
		@Valid OrderRegisterRequest orderTempRegisterRequest);

	/**
	 * 비회원 주문 임시 등록
	 *
	 * @param orderTempRegisterRequest 주문 임시 등록 request
	 * @return 주문 임시 등록 response
	 */
	OrderRegisterResponse registerTempOrderForCustomer(@Valid OrderRegisterRequest orderTempRegisterRequest);

	/**
	 * 주문 금액 검증
	 *
	 * @param paymentRequest 토스 페이먼츠에서 전달받은 데이터
	 * @return 성공/실패 메시지
	 */
	ResponseMessage verifyOrderInformation(PaymentRequest paymentRequest);

	/**
	 * 주문 목록 조회
	 *
	 * @param includeOrdersInPendingStatus 대기 미포함 여부
	 * @param pageable 페이지
	 * @param userId 사용자 아이디
	 * @return 주문 목록
	 */
	Page<OrderListResponse> getOrderList(
		boolean includeOrdersInPendingStatus,
		Pageable pageable,
		OrderListPeriodRequest orderListPeriodRequest,
		Optional<Long> userId);

	Page<OrderListResponse> getNonMemberOrderList(
		boolean includeOrdersInPendingStatus,
		Pageable pageable,
		OrderListPeriodRequest orderListPeriodRequest,
		Optional<Long> userId);

	/**
	 * 주문 상세 조회
	 * @param memberId 사용자 아이디
	 * @param orderId 주문 아이디
	 * @param pageable 페이ㅣ
	 * @return 주문 상세 내역
	 */
	OrderDetailResponse getOrderDetail(Optional<Long> memberId, Long orderId, Pageable pageable);

	/**
	 * 주문 취소/환불 목록 가져오기
	 *
	 * @param pageable 페이지
	 * @param orderListPeriodRequest 적용 날짜
	 * @param memberId 사용자 아이디
	 * @return 주문 취소/환불 목록
	 */
	Page<OrderListResponse> getCancelledOrderList(Pageable pageable, OrderListPeriodRequest orderListPeriodRequest,
		Optional<Long> memberId);

	/**
	 * 주문 취소 폼 불러오기
	 * @param memberId 사용자 아이디
	 * @param orderId 주문 아이디
	 * @return 주문 취소 폼
	 */
	OrderCancelDto getOrderCancel(Optional<Long> memberId, Long orderId);
}
