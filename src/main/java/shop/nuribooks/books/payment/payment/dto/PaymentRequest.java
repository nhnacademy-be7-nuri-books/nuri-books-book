package shop.nuribooks.books.payment.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaymentRequest(
	@JsonProperty("orderId") String orderId,
	@JsonProperty("amount") int amount,
	@JsonProperty("paymentKey") String paymentKey
) {
}
