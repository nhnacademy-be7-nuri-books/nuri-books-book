package shop.nuribooks.books.order.orderDetail.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.orderDetail.entity.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}
