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

	//소수점 버리기 (1원 단위로 계산 위해)
	public static BigDecimal calculateSalePrice(BigDecimal price, int discountRate) {
		return price.multiply(BigDecimal.valueOf(100L - discountRate))
			.divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN);
	}

	//작가역할에 따른 작가리스트 그룹핑
	public static Map<String, List<String>> groupContributorsByRole(List<BookContributorInfoResponse> contributors) {
		return contributors.stream()
			.collect(Collectors.groupingBy(
				BookContributorInfoResponse::contributorRoleName,
				Collectors.mapping(BookContributorInfoResponse::contributorName, Collectors.toList())
			));
	}

	public static BigDecimal calculateTotalPrice(BigDecimal price, int quantity) {
		return price.multiply(BigDecimal.valueOf(quantity)).setScale(0, RoundingMode.DOWN);
	}
}
