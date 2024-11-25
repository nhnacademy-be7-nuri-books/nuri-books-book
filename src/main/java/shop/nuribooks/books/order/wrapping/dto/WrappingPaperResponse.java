package shop.nuribooks.books.order.wrapping.dto;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record WrappingPaperResponse(
	Long id,
	String title,
	String imageUrl,
	BigDecimal wrappingPrice
) {
}
