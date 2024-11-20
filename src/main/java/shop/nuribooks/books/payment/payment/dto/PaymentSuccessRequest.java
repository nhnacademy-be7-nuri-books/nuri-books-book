package shop.nuribooks.books.payment.payment.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record PaymentSuccessRequest(
	String status,
	String orderId,
	String paymentKey,
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	LocalDateTime requestedAt,
	String method,
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	LocalDateTime approvedAt,
	Long totalAmount
) {
}