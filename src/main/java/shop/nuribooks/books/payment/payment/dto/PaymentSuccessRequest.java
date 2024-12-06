package shop.nuribooks.books.payment.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.payment.payment.entity.Payment;
import shop.nuribooks.books.payment.payment.entity.PaymentMethod;
import shop.nuribooks.books.payment.payment.entity.PaymentState;

// @Builder
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
	public Payment toEntity(Order order) {
		return Payment.builder()
			.order(order)
			.tossPaymentKey(this.paymentKey())
			.paymentMethod(PaymentMethod.fromKoreanName(this.method()))
			.paymentState(PaymentState.valueOf(this.status()))
			.unitPrice(BigDecimal.valueOf(this.totalAmount()))
			.requestedAt(this.requestedAt())
			.approvedAt(this.approvedAt())
			.build();
	}
}
