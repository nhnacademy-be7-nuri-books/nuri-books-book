// package shop.nuribooks.books.book.review.dto.response;
//
// import lombok.Builder;
// import shop.nuribooks.books.book.review.entity.ReviewImage;
//
// @Builder
// public record ReviewImageResponse(
// 	long id,
// 	String imageUrl
// ) {
// 	public static ReviewImageResponse of(ReviewImage reviewImage) {
// 		return ReviewImageResponse.builder()
// 			.id(reviewImage.getId())
// 			.imageUrl(reviewImage.getImageUrl())
// 			.build();
// 	}
// }
