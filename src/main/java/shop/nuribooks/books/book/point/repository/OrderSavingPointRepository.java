package shop.nuribooks.books.book.point.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.point.entity.child.OrderSavingPoint;
import shop.nuribooks.books.order.order.entity.Order;

public interface OrderSavingPointRepository extends JpaRepository<OrderSavingPoint, Long> {
	Optional<OrderSavingPoint> findByOrder(Order order);
}
