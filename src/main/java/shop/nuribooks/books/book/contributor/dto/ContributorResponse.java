package shop.nuribooks.books.book.contributor.dto;

import lombok.Builder;
import shop.nuribooks.books.book.contributor.entity.Contributor;

@Builder
public record ContributorResponse(Long id, String name) {
	public static ContributorResponse of(Contributor contributor) {
		return ContributorResponse.builder()
			.id(contributor.getId())
			.name(contributor.getName())
			.build();
	}
}

