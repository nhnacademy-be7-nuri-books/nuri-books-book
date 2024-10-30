package shop.nuribooks.books.book.booktag.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BookTagRequest(@NotNull Long bookId, @NotNull List<Long> tagId) {
	public BookTagRequest toEntity() {
		return BookTagRequest.builder()
			.bookId(bookId)
			.tagId(tagId)
			.build();
	}
}
