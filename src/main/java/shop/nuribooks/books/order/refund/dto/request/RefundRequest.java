package shop.nuribooks.books.order.refund.dto.request;

import java.math.BigDecimal;

import shop.nuribooks.books.order.orderDetail.entity.OrderDetail;
import shop.nuribooks.books.order.refund.entity.Refund;

public record RefundRequest(Long orderDetailId,
							String reason) {

	public Refund toEntity(OrderDetail orderDetail, BigDecimal refundAmount) {
		return Refund.builder()
			.orderDetail(orderDetail)
			.refundAmount(refundAmount)
			.reason(reason)
			.build();
	}
}
