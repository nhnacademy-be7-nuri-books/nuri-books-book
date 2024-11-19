package shop.nuribooks.books.book.point.dto.request;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointHistoryPeriodRequest {
	private LocalDate end = LocalDate.now();
	private LocalDate start = end.minusDays(30);
}
