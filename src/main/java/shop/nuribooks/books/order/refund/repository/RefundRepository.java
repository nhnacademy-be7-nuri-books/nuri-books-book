package shop.nuribooks.books.order.refund.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.order.refund.entity.Refund;

public interface RefundRepository extends JpaRepository<Refund, Long>, RefundCustomRepository {

}
