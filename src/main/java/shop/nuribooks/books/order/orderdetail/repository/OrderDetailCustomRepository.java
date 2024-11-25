package shop.nuribooks.books.order.orderdetail.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.order.orderdetail.dto.OrderDetailItemPageDto;
import shop.nuribooks.books.order.orderdetail.entity.OrderDetail;

public interface OrderDetailCustomRepository {
	List<OrderDetail> findAllByOrderId(Long orderId);

	OrderDetailItemPageDto findOrderDetail(Long orderId, Pageable pageable);
}
