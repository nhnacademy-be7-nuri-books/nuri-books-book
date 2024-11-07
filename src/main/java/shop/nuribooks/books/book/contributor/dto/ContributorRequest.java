package shop.nuribooks.books.book.contributor.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import shop.nuribooks.books.book.contributor.entity.Contributor;

@Builder
public record ContributorRequest(@NotBlank @Length(min = 1, max = 50) String name) {
	public Contributor toEntity() {
		return Contributor.builder()
			.name(name)
			.build();
	}
}