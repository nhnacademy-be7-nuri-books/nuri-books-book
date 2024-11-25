package shop.nuribooks.books.order.returnrequest.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.order.returnrequest.dto.request.ReturnRequestRegisterRequest;
import shop.nuribooks.books.order.returnrequest.dto.response.ReturnRequestResponse;

/**
 * 포장지 서비스 인터페이스
 *
 * @author nuri
 */
public interface ReturnRequestService {

	void registerReturnRequest(Long orderId, ReturnRequestRegisterRequest returnRequestRegisterRequest);

	Page<ReturnRequestResponse> getReturnRequest(Pageable pageable);
}
