package shop.nuribooks.books.book.book.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import shop.nuribooks.books.book.book.entitiy.Book;

public record BookRegisterResponse(
	Long id,
	Long publisherId,
	String korName,
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
			book.getPublisherId().getId(),
			book.getState().getKorName(),
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
