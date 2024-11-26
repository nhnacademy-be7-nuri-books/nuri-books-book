package shop.nuribooks.books.order.refund.dto.request;

import java.math.BigDecimal;

import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.refund.entity.Refund;

public record RefundRequest(BigDecimal refundAmount,
							String reason) {

	public Refund toEntity(Order order) {
		return Refund.builder()
			.order(order)
			.refundAmount(refundAmount)
			.reason(reason)
			.build();
	}
}
