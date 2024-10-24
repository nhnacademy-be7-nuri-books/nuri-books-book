package shop.nuribooks.books.dto.book;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import shop.nuribooks.books.entity.book.Book;

public record BookRegisterRes(
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
	public static BookRegisterRes of(Book book) {
		return new BookRegisterRes(
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
