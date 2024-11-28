package shop.nuribooks.books.book.point.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.point.entity.child.OrderUsingPoint;
import shop.nuribooks.books.order.order.entity.Order;

public interface OrderUsingPointRepository extends JpaRepository<OrderUsingPoint, Long> {
	Optional<OrderUsingPoint> findByOrder(Order order);
}
