package shop.nuribooks.books.payment.payment.dto;

import java.time.OffsetDateTime;

public record PaymentSuccessRequest(
	String status,
	String orderId,
	String paymentKey,
	OffsetDateTime requestedAt,
	String method,
	OffsetDateTime approvedAt,
	int totalAmount
)
{}
