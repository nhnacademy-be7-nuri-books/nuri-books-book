package shop.nuribooks.books.book.point.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PointHistoryResponse {
	long getId();

	BigDecimal getAmount();

	String getDescription();

	LocalDateTime getCreatedAt();
}
