package shop.nuribooks.books.order.order.service;

import jakarta.validation.Valid;
import shop.nuribooks.books.common.message.ResponseMessage;
import shop.nuribooks.books.order.order.dto.OrderInformationResponse;
import shop.nuribooks.books.order.order.dto.OrderTempRegisterRequest;
import shop.nuribooks.books.order.order.dto.OrderTempRegisterResponse;
import shop.nuribooks.books.payment.payment.dto.PaymentRequest;
import shop.nuribooks.books.payment.payment.dto.PaymentSuccessRequest;

/**
 * 주문 서비스 인터페이스
 *
 * @author nuri
 */
public interface OrderService {

	/**
	 * 주문 폼 정보 가져오기 - 바로 주문(회원)
	 *
	 * @param id 사용자 아이디
	 * @param bookId 상품 아이디
	 * @param quantity 상품 갯수
	 * @return OrderInformationResponse
	 */
	OrderInformationResponse getMemberOrderInformation(Long id, Long bookId, int quantity);

	/**
	 * 주문 폼 정보 가져오기 - 바로 주문(비회원)
	 *
	 * @param bookId 상품 아이디
	 * @param quantity 상품 갯수
	 * @return OrderInformationResponse
	 */
	OrderInformationResponse getCustomerOrderInformation(Long bookId, int quantity);

	/**
	 * 회원 주문 임시 등록
	 *
	 * @param id UserId pk
	 * @param orderTempRegisterRequest 주문 임시 등록 request
	 * @return 주문 임시 등록 response
	 */
	OrderTempRegisterResponse registerTempOrderForMember(Long id, @Valid OrderTempRegisterRequest orderTempRegisterRequest);

	/**
	 * 비회원 주문 임시 등록
	 *
	 * @param orderTempRegisterRequest 주문 임시 등록 request
	 * @return 주문 임시 등록 response
	 */
	OrderTempRegisterResponse registerTempOrderForCustomer(@Valid OrderTempRegisterRequest orderTempRegisterRequest);

	/**
	 * 주문 금액 검증
	 *
	 * @param paymentRequest 토스 페이먼츠에서 전달받은 데이터
	 * @return 성공/실패 메시지
	 */
	ResponseMessage verifyOrderInformation(PaymentRequest paymentRequest);

}
