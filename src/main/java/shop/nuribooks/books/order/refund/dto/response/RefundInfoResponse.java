package shop.nuribooks.books.order.refund.dto.response;

import org.springframework.data.domain.Page;

import shop.nuribooks.books.order.orderdetail.dto.OrderDetailItemDto;
import shop.nuribooks.books.payment.payment.dto.PaymentInfoDto;

public record RefundInfoResponse(
	Page<OrderDetailItemDto> orderItems, // 주문 항목 리스트
	PaymentInfoDto payment,
	RefundInfo refundInfo) {
}
