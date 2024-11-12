package shop.nuribooks.books.order.orderDetail.service;

import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.orderDetail.dto.OrderDetailRequest;

/**
 * 주문 상세 서비스 인터페이스
 *
 * @author nuri
 */
public interface OrderDetailService {
	void registerOrderDetail(Order order, OrderDetailRequest orderDetailRequest);

	String getBookTitle(Long bookId);
}
