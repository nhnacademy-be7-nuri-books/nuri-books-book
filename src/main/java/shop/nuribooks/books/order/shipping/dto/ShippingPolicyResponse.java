package shop.nuribooks.books.order.shipping.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record ShippingPolicyResponse(
	Long id,
	Integer shippingFee,
	LocalDateTime expiration,
	BigDecimal minimumOrderPrice
) {
}
