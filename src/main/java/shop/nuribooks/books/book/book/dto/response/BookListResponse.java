package shop.nuribooks.books.book.book.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.entity.BookStateEnum;

public record BookListResponse(
	Long id,
	String publisherName,
	BookStateEnum state,
	String title,
	LocalDate publicationDate,
	BigDecimal price,
	Integer discountRate,
	String thumbnailImageUrl
) {
	public static BookListResponse of(Book book) {
		return new BookListResponse(
			book.getId(),
			book.getPublisherId().getName(),
			book.getState(),
			book.getTitle(),
			book.getPublicationDate(),
			book.getPrice(),
			book.getDiscountRate(),
			book.getThumbnailImageUrl()
		);
	}
}
