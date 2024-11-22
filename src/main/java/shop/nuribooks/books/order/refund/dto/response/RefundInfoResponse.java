package shop.nuribooks.books.order.refund.dto.response;

import java.math.BigDecimal;

public record RefundInfoResponse(BigDecimal refundAmount,
								 BigDecimal deductedAmount,
								 BigDecimal totalRefundAmount) {
}
