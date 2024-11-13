package shop.nuribooks.books.order.order.service;

import jakarta.validation.Valid;
import shop.nuribooks.books.order.order.dto.OrderTempRegisterRequest;
import shop.nuribooks.books.order.order.dto.OrderTempRegisterResponse;

/**
 * 주문 서비스 인터페이스
 *
 * @author nuri
 */
public interface OrderService {

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
}
