package shop.nuribooks.books.book.point.dto.request;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PointHistoryPeriodRequest {
	private LocalDateTime end = LocalDateTime.now();
	private LocalDateTime start = end.minusDays(30);
}
