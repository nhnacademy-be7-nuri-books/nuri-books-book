package shop.nuribooks.books.book.review.dto.response;

import lombok.Builder;

@Builder
public record ReviewImageResponse(
	long id,
	String imageUrl
) {
}
