package shop.nuribooks.books.payment.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.payment.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
