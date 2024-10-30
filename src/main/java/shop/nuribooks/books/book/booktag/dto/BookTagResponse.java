package shop.nuribooks.books.book.booktag.dto;

import java.util.List;

import lombok.Builder;
import shop.nuribooks.books.book.book.entitiy.Book;

@Builder
public record BookTagResponse(Long bookTagId, Long bookId, List<Long> tagId) {
	public static BookTagResponse of(Book book, Long bookId, List<Long> tagIds) {
		return new BookTagResponse(book.getId(), bookId, tagIds);
	}
}
