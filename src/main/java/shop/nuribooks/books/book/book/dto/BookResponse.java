package shop.nuribooks.books.book.book.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;

import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.category.dto.CategoryResponse;
import shop.nuribooks.books.book.category.entitiy.Category;
import shop.nuribooks.books.book.contributor.entitiy.BookContributor;
import shop.nuribooks.books.book.publisher.entitiy.Publisher;

public record BookResponse(
	Long id,
	String publisherName,
	String state,
	String title,
	String thumbnailImageUrl,
	String detailImageUrl,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	LocalDate publicationDate,
	BigDecimal price,
	int discountRate,
	String description,
	String contents,
	String isbn,
	boolean isPackageable,
	int likeCount,
	int stock,
	Long viewCount,
	List<ContributorInfoResponse> contributors,
	List<CategoryResponse> categories
) {
	public static BookResponse of(Book book, List<BookContributor> bookContributors, List<Category> categories) {
		List<ContributorInfoResponse> contributorInfoResponses = bookContributors.stream()
			.map(contributor -> new ContributorInfoResponse(
				contributor.getContributor().getName(),
				contributor.getContributorRole().getName().name()
			))
			.collect(Collectors.toList());

		List<CategoryResponse> categoryResponses = categories.stream()
			.map(CategoryResponse::new)
			.collect(Collectors.toList());

		return new BookResponse(
			book.getId(),
			book.getPublisherId().getName(),
			book.getState().getKorName(),
			book.getTitle(),
			book.getThumbnailImageUrl(),
			book.getDetailImageUrl(),
			book.getPublicationDate(),
			book.getPrice(),
			book.getDiscountRate(),
			book.getDescription(),
			book.getContents(),
			book.getIsbn(),
			book.isPackageable(),
			book.getLikeCount(),
			book.getStock(),
			book.getViewCount(),
			contributorInfoResponses,
			categoryResponses
		);
	}
}