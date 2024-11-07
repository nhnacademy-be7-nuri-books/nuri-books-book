package shop.nuribooks.books.book.book.dto;

import lombok.Builder;

@Builder
public record BookBriefResponse(Long id,
								String title,
								String thumbnailImageUrl) {
}