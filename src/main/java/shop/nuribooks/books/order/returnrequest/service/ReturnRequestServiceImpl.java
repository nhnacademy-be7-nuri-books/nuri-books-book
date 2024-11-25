package shop.nuribooks.books.order.returnrequest.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.exception.order.OrderNotFoundException;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.order.repository.OrderRepository;
import shop.nuribooks.books.order.returnrequest.dto.request.ReturnRequestRegisterRequest;
import shop.nuribooks.books.order.returnrequest.entity.ReturnRequest;
import shop.nuribooks.books.order.returnrequest.repository.ReturnRequestRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReturnRequestServiceImpl implements ReturnRequestService {

	private final ReturnRequestRepository returnRequestRepository;
	private final OrderRepository orderRepository;

	@Transactional
	@Override
	public void registerReturnRequest(Long orderId, ReturnRequestRegisterRequest returnRequestRegisterRequest) {
		Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("주문이 없습니다."));
		ReturnRequest returnRequest = returnRequestRegisterRequest.toEntity(order);
		returnRequestRepository.save(returnRequest);
	}
}
