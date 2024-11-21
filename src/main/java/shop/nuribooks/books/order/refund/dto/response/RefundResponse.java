package shop.nuribooks.books.order.refund.dto.response;

import java.math.BigDecimal;

import lombok.Builder;
import shop.nuribooks.books.order.orderDetail.entity.OrderDetail;
import shop.nuribooks.books.order.refund.entity.Refund;

@Builder
public record RefundResponse(Long id,
							 OrderDetail orderDetail,
							 String reason,
							 BigDecimal refundAmount) {
	public static RefundResponse of(Refund refund) {
		return RefundResponse.builder()
			.id(refund.getId())
			.orderDetail(refund.getOrderDetail())
			.reason(refund.getReason())
			.refundAmount(refund.getRefundAmount())
			.build();
	}
}
