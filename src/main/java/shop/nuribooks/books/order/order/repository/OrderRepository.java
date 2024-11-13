package shop.nuribooks.books.order.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.order.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
