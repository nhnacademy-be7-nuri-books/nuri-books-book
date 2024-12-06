package shop.nuribooks.books.book.book.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import shop.nuribooks.books.book.category.dto.SimpleCategoryResponse;

public record BookResponse(
	Long id,
	String publisherName,
	String state,
	String title,
	String thumbnailImageUrl,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	LocalDate publicationDate,
	BigDecimal price,
	int discountRate,
	BigDecimal salePrice,
	String description,
	String contents,
	String isbn,
	boolean isPackageable,
	int likeCount,
	int stock,
	Long viewCount,
	List<String> tagNames,
	Map<String, List<String>> contributorsByRole,
	List<List<SimpleCategoryResponse>> simpleCategories
) {
}