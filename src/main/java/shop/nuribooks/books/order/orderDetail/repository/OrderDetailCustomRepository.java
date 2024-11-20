package shop.nuribooks.books.order.orderDetail.repository;

import java.util.List;

import shop.nuribooks.books.order.orderDetail.entity.OrderDetail;
import shop.nuribooks.books.order.shipping.entity.ShippingPolicy;

public interface OrderDetailCustomRepository {
	List<OrderDetail> findAllByOrderId(Long orderId);
}
