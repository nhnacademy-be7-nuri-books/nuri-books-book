package shop.nuribooks.books.order.refund.dto.response;

import java.math.BigDecimal;

public record RefundInfo(BigDecimal paymentAmount,
						 BigDecimal deductedAmount,
						 BigDecimal totalRefundAmount) {
}
