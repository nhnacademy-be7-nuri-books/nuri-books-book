package shop.nuribooks.books.book.booktag.dto;

import java.util.List;

import lombok.Builder;
import shop.nuribooks.books.book.book.entity.Book;

@Builder
public record BookTagRegisterResponse(Long bookTagId, Long bookId, List<Long> tagIds) {
	public static BookTagRegisterResponse of(Book book, Long bookId, List<Long> tagIds) {
		return new BookTagRegisterResponse(book.getId(), bookId, tagIds);
	}
}
