package shop.nuribooks.books.order.returnrequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.order.returnrequest.entity.ReturnRequest;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {
}
