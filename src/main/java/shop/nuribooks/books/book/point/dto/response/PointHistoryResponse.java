package shop.nuribooks.books.book.point.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PointHistoryResponse(long id, BigDecimal amount, String description, LocalDateTime createdAt) {
}
