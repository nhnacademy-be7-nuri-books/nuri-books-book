package shop.nuribooks.books.book.booktag.dto;

import java.util.List;

import lombok.Builder;
import shop.nuribooks.books.book.book.entitiy.Book;

@Builder
public record BookTagResponse(List<Long> bookTagIds, Long bookId, List<Long> tagIds) {
	public static BookTagResponse of(List<Long> bookTagIds, Long bookId, List<Long> tagIds) {
		return new BookTagResponse(bookTagIds, bookId, tagIds);
	}
}
