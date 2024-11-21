package shop.nuribooks.books.order.orderDetail.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.order.orderDetail.entity.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long>, OrderDetailCustomRepository {
	List<OrderDetail> findByBookIdAndOrderCustomerIdAndReviewIsNull(long bookId, long customerId);
}
