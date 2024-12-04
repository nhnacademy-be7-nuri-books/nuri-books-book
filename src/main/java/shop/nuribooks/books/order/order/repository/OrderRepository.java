package shop.nuribooks.books.order.order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.order.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderCustomerRepository {
	Optional<Order> findByIdAndCustomer(Long orderId, Customer customer);
}
