package shop.nuribooks.books.order.refund.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RefundInfoDto(
	BigDecimal paymentPrice,
	BigDecimal orderSavingPoint,
	LocalDateTime shippingAt
) {
}
