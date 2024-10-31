package shop.nuribooks.books.book.review.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import shop.nuribooks.books.member.member.entity.Member;

@Builder
public record ReviewResponse(
	long id,
	String title,
	String content,
	int score,
	LocalDateTime createdAt,
	boolean isUpdated,
	Member member,
	List<ReviewImageResponse> reviewImageResponses
) {
	// public static ReviewResponse of(Review review) {
	// 	return ReviewResponse.builder()
	// 		.id(review.getId())
	// 		.title(review.getTitle())
	// 		.content(review.getContent())
	// 		.score(review.getScore())
	// 		.createdAt(review.getCreatedAt())
	// 		.isUpdated(review.getCreatedAt().isBefore(review.getUpdateAt()))
	// 		.member(review.getMember())
	// 		.build();
	// }
}
