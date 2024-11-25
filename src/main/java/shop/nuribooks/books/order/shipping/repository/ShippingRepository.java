package shop.nuribooks.books.order.shipping.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.shipping.entity.Shipping;

public interface ShippingRepository extends JpaRepository<Shipping, Long> {

	Shipping findByOrder(Order order);

	Page<Shipping> findAll(Pageable pageable);
}
