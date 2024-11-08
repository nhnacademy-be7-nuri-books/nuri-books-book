package shop.nuribooks.books.book.book.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import shop.nuribooks.books.book.book.entity.Book;

public record BookRegisterResponse(
	Long id,
	String publisherName,
	String korName,
	String title,
	String thumbnailImageUrl,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	LocalDate publicationDate,
	BigDecimal price,
	int discountRate,
	String description,
	int stock
) {
	public static BookRegisterResponse of(Book book) {
		return new BookRegisterResponse(
			book.getId(),
			book.getPublisherId().getName(),
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
