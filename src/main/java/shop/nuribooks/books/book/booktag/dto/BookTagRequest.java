package shop.nuribooks.books.book.booktag.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record BookTagRequest(@NotNull(message = "bookId는 필수 입력 항목입니다")
							 @Positive(message = "bookId는 양수여야 합니다")
							 Long bookId,

							 @NotNull(message = "tagId는 필수 입력 항목입니다")
							 List<@Positive(message = "tagId는 양수여야 합니다") Long> tagId) {
	public BookTagRequest toEntity() {
		return BookTagRequest.builder()
			.bookId(bookId)
			.tagId(tagId)
			.build();
	}
}
