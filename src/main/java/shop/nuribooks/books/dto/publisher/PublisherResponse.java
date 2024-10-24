package shop.nuribooks.books.dto.publisher;

import lombok.Builder;
import shop.nuribooks.books.entity.book.Publisher;

@Builder
public record PublisherResponse(Long id, String name) {

	public static PublisherResponse of(Publisher publisher) {
		return PublisherResponse.builder()
			.id(publisher.getId())
			.name(publisher.getName())
			.build();
	}
}
