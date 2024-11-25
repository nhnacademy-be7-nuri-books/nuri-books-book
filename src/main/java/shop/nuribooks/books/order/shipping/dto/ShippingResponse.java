package shop.nuribooks.books.order.shipping.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import shop.nuribooks.books.order.shipping.entity.ShippingPolicy;

@Builder
public record ShippingResponse(
	Long id,
	Long orderId,
	ShippingPolicy shippingPolicy,
	String recipientName,
	String recipientAddress,
	String recipientAddressDetail,
	String recipientZipcode,
	String recipientPhoneNumber,
	String senderName,
	String senderPhoneNumber,
	LocalDateTime shippingAt,
	String orderInvoiceNumber,
	LocalDateTime shippingCompletedAt
) {
}
