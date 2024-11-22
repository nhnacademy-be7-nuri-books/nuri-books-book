package shop.nuribooks.books.order.orderdetail.repository;

import java.util.List;

import shop.nuribooks.books.order.orderdetail.entity.OrderDetail;

public interface OrderDetailCustomRepository {
	List<OrderDetail> findAllByOrderId(Long orderId);
}
