package shop.nuribooks.books.order.wrapping.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record WrappingPaperResponse(
        Long id,
        String title,
        String imageUrl,
        BigDecimal wrapping_price
) {
}
