package shop.nuribooks.books.order.order.repository;

import java.math.BigDecimal;

import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.order.order.dto.request.OrderListPeriodRequest;
import shop.nuribooks.books.order.order.dto.response.OrderPageResponse;
import shop.nuribooks.books.payment.payment.dto.PaymentInfoDto;

public interface OrderCustomerRepository {
	OrderPageResponse findOrders(boolean includeOrdersInPendingStatus, Long memberId, Pageable pageable,
		OrderListPeriodRequest orderListPeriodRequest);

	OrderPageResponse findNonMemberOrders(boolean includeOrdersInPendingStatus, Long customerId, Pageable pageable,
		OrderListPeriodRequest orderListPeriodRequest);

	PaymentInfoDto findPaymentInfo(Long orderId);


	OrderPageResponse findCancelledOrders(Long memberId, Pageable pageable,
		OrderListPeriodRequest orderListPeriodRequest);

	BigDecimal findOrderSavingPoint(Long orderId);

}
