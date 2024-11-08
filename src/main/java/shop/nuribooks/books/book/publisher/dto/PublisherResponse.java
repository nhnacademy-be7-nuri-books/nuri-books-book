package shop.nuribooks.books.book.publisher.dto;

import lombok.Builder;
import shop.nuribooks.books.book.publisher.entity.Publisher;

@Builder
public record PublisherResponse(Long id, String name) {

	public static PublisherResponse of(Publisher publisher) {
		return PublisherResponse.builder()
			.id(publisher.getId())
			.name(publisher.getName())
			.build();
	}
}
