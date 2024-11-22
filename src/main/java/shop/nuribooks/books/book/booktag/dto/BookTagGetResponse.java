package shop.nuribooks.books.book.booktag.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record BookTagGetResponse(Long bookTagId, Long bookId, List<String> tagNames) {
	public static BookTagGetResponse of(Long bookTagId, Long bookId, List<String> tagNames) {
		return new BookTagGetResponse(bookTagId, bookId, tagNames);
	}
}
