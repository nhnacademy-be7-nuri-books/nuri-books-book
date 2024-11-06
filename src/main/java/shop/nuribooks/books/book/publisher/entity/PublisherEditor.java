package shop.nuribooks.books.book.publisher.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PublisherEditor {
	private String name;

	@Builder
	private PublisherEditor(String name) {
		this.name = name;
	}
}
