package shop.nuribooks.books.order.returnrequest.service;

import shop.nuribooks.books.order.returnrequest.dto.request.ReturnRequestRegisterRequest;

/**
 * 포장지 서비스 인터페이스
 *
 * @author nuri
 */
public interface ReturnRequestService {

	void registerReturnRequest(Long orderId, ReturnRequestRegisterRequest returnRequestRegisterRequest);

}
