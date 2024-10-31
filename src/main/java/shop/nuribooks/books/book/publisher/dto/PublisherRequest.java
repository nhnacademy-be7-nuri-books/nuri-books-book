package shop.nuribooks.books.book.publisher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import shop.nuribooks.books.book.publisher.entitiy.Publisher;

@Builder
public record PublisherRequest(@NotBlank String name) {

	public Publisher toEntity() {
		return Publisher.builder()
			.name(name)
			.build();
	}
}
