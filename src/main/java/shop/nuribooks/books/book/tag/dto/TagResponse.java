package shop.nuribooks.books.book.tag.dto;

import lombok.Builder;
import shop.nuribooks.books.book.tag.entity.Tag;

@Builder
public record TagResponse(Long id, String name) {
	public static TagResponse of(Tag tag) {
		return TagResponse.builder()
			.id(tag.getId())
			.name(tag.getName())
			.build();
	}
}
