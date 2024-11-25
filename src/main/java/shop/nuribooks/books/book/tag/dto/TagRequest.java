package shop.nuribooks.books.book.tag.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import shop.nuribooks.books.book.tag.entity.Tag;

@Builder
public record TagRequest(@NotBlank String name) {

	public Tag toEntity() {
		return Tag.builder()
			.name(name)
			.build();
	}
}
