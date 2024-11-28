package shop.nuribooks.books.order.order.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import shop.nuribooks.books.order.wrapping.dto.WrappingPaperResponse;

@Builder
public record OrderSummaryDto(
	String title,
	LocalDateTime orderedAt,
	WrappingPaperResponse wrappingInfo
) {
}
