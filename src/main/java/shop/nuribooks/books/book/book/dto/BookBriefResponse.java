package shop.nuribooks.books.book.book.dto;

import lombok.Builder;
import shop.nuribooks.books.book.book.entity.Book;

@Builder
public record BookBriefResponse(Long id,
								String title,
								String thumbnailImageUrl) {
	public static BookBriefResponse of(Book book) {
		return builder().id(book.getId())
			.title(book.getTitle())
			.thumbnailImageUrl(book.getThumbnailImageUrl())
			.build();
	}
}