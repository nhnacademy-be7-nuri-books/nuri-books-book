package shop.nuribooks.books.dto.publisher;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import shop.nuribooks.books.entity.book.Publisher;

@Builder
public record PublisherRequest(@NotNull String name) {

	public Publisher toEntity() {
		return Publisher.builder()
			.name(name)
			.build();
	}
}
