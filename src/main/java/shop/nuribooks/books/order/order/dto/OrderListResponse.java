package shop.nuribooks.books.order.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import shop.nuribooks.books.order.orderdetail.entity.OrderState;

public record OrderListResponse(
	Long orderId,
	LocalDateTime orderedAt,
	String title,
	BigDecimal paymentPrice,
	String orderInvoiceNumber,
	OrderState orderState
) {
}
