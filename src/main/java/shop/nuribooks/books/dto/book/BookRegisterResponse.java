package shop.nuribooks.books.dto.book;

import java.math.BigDecimal;
import java.time.LocalDate;

import shop.nuribooks.books.entity.book.Book;

public record BookRegisterResponse(
	Long id,
	Integer stateId,
	Long publisherId,
	String title,
	String thumbnailImageUrl,
	LocalDate publicationDate,
	BigDecimal price,
	int discountRate,
	String description,
	int stock
) {
	public static BookRegisterResponse of(Book book) {
		return new BookRegisterResponse(
			book.getId(),
			book.getStateId().getId(),
			book.getPublisherId().getId(),
			book.getTitle(),
			book.getThumbnailImageUrl(),
			book.getPublicationDate(),
			book.getPrice(),
			book.getDiscountRate(),
			book.getDescription(),
			book.getStock()
		);
	}
}
