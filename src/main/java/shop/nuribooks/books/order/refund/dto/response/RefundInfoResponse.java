package shop.nuribooks.books.order.refund.dto.response;

import java.math.BigDecimal;

public record RefundInfoResponse(
	Long orderId,
	BigDecimal paymentAmount,
	BigDecimal deductedAmount,
	BigDecimal totalRefundAmount) {
}
