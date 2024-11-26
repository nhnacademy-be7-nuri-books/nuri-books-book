package shop.nuribooks.books.order.refund.dto.response;

import java.math.BigDecimal;

import lombok.Builder;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.refund.entity.Refund;

@Builder
public record RefundResponse(Long id,
							 Order order,
							 String reason,
							 BigDecimal refundAmount) {
	public static RefundResponse of(Refund refund) {
		return RefundResponse.builder()
			.id(refund.getId())
			.order(refund.getOrder())
			.reason(refund.getReason())
			.refundAmount(refund.getRefundAmount())
			.build();
	}
}
