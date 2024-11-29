package shop.nuribooks.books.payment.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.payment.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	Optional<Payment> findByOrder(Order order);
}
