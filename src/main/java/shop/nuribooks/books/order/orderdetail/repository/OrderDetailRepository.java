package shop.nuribooks.books.order.orderdetail.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.order.orderdetail.entity.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long>, OrderDetailCustomRepository {
	List<OrderDetail> findByBookIdAndOrderCustomerIdAndReviewIsNullAndOrderStateIn(long bookId, long customerId,
		List<Integer> orderStates);
}
