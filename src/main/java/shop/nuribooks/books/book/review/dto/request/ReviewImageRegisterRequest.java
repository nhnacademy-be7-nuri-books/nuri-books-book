package shop.nuribooks.books.book.review.dto.request;

import jakarta.validation.constraints.NotBlank;
import shop.nuribooks.books.book.review.entity.Review;
import shop.nuribooks.books.book.review.entity.ReviewImage;

public record ReviewImageRegisterRequest(
	@NotBlank(message = "리뷰 이미지 url이 필요합니다.")
	String imageUrl
) {
	public ReviewImage toEntity(Review review) {
		return ReviewImage.builder()
			.imageUrl(this.imageUrl)
			.review(review)
			.build();
	}
}
