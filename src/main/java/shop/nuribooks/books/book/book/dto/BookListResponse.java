package shop.nuribooks.books.book.book.dto;

import java.math.BigDecimal;

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.entity.BookStateEnum;

public record BookListResponse(
	Long id,
	String publisherName,
	BookStateEnum state,
	String title,
	BigDecimal price,
	Integer discountRate,
	String thumbnailImageUrl,
	Long reviewCount,
	Double scoreAvg
) {
	public static BookListResponse of(Book book) {
		return new BookListResponse(
			book.getId(),
			book.getPublisherId().getName(),
			book.getState(),
			book.getTitle(),
			book.getPrice(),
			book.getDiscountRate(),
			book.getThumbnailImageUrl(),
			0l,
			0d
		);
	}
}
