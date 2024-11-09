package shop.nuribooks.books.book.book.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;

public class BookUtils {
	private BookUtils() {

	}

	public static BigDecimal calculateSalePrice(BigDecimal price, int discountRate) {
		return price.multiply(BigDecimal.valueOf(100 - discountRate))
			.divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN);
	}

	public static Map<String, List<String>> groupContributorsByRole(List<BookContributorInfoResponse> contributors) {
		return  contributors.stream()
			.collect(Collectors.groupingBy(
				BookContributorInfoResponse::contributorRoleName,
				Collectors.mapping(BookContributorInfoResponse::contributorName, Collectors.toList())
			));
	}
}
