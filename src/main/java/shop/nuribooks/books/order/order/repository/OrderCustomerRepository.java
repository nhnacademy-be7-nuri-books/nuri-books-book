package shop.nuribooks.books.order.order.repository;

import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.order.order.dto.request.OrderListPeriodRequest;
import shop.nuribooks.books.order.order.dto.response.OrderPageResponse;

public interface OrderCustomerRepository {
	OrderPageResponse findOrders(boolean includeOrdersInPendingStatus, Long userId, Pageable pageable,
		OrderListPeriodRequest orderListPeriodRequest);
}
