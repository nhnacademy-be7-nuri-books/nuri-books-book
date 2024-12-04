package shop.nuribooks.books.payment.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.payment.payment.entity.PaymentCancel;

public interface PaymentCancelRepository extends JpaRepository<PaymentCancel, Long> {
}
