package shop.nuribooks.books.order.refund.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RefundInfoDto(
	LocalDateTime shippingAt,
	BigDecimal paymentPrice,
	BigDecimal orderSavingPoint
) {
}
