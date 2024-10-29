package shop.nuribooks.books.book.book.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.bookstate.entitiy.BookState;
import shop.nuribooks.books.book.publisher.entitiy.Publisher;

public record BookResponse(
	Long id,
	BookState state,
	Publisher publisher,
	String title,
	String thumbnailImageUrl,
	String detailImageUrl,
	LocalDate publicationDate,
	BigDecimal price,
	int discountRate,
	String description,
	String contents,
	String isbn,
	boolean isPackageable,
	int likeCount,
	int stock,
	Long viewCount
) {
	public static BookResponse of(Book book) {
		return new BookResponse(
			book.getId(),
			book.getStateId(),
			book.getPublisherId(),
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
			book.getViewCount()
		);
	}
}