package shop.nuribooks.books.book.booklike.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.utility.BookUtils;

@Builder(toBuilder = true)
public record BookLikeResponse(
	Long bookId,
	String title,
	Map<String, List<String>> contributorsByRole,
	String publisherName,
	BigDecimal price,
	int discountRate,
	BigDecimal salePrice,
	String thumbnailImageUrl
) {
	public static BookLikeResponse of(Book book, Map<String, List<String>> contributorsByRole) {
		return new BookLikeResponse(
			book.getId(),
			book.getTitle(),
			contributorsByRole,
			book.getPublisherId().getName(),
			book.getPrice(),
			book.getDiscountRate(),
			BookUtils.calculateSalePrice(book.getPrice(), book.getDiscountRate()),
			book.getThumbnailImageUrl()
		);
	}
}
