package shop.nuribooks.books.book.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewUpdateRequest(
	@NotBlank(message = "리뷰 제목은 필수입니다.")
	@Size(max = 255, message = "리뷰 제목은 최대 255자까지 가능합니다.")
	String title,

	@NotBlank(message = "리뷰 내용은 필수입니다.")
	@Size(min = 10, max = 65535, message = "리뷰 내용은 최소 10자, 최대 60000자까지 가능합니다.")
	String content,

	@NotNull(message = "리뷰 별점은 필수입니다.")
	@Min(value = 1, message = "별점은 1점 이상이어야 합니다.")
	@Max(value = 5, message = "별점은 5점 이하여야 합니다.")
	int score,
	
	long bookId
) {
}
