package shop.nuribooks.books.order.orderDetail.service;

import java.util.List;
import java.util.Optional;

import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.orderDetail.dto.OrderDetailRequest;
import shop.nuribooks.books.order.orderDetail.entity.OrderDetail;

/**
 * 주문 상세 서비스 인터페이스
 *
 * @author nuri
 */
public interface OrderDetailService {
	void registerOrderDetail(Order order, OrderDetailRequest orderDetailRequest);

	String getBookTitle(Long bookId);

	boolean checkStock(Order order);
}
