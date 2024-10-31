package shop.nuribooks.books.book.review.dto.response;

import lombok.Builder;
import shop.nuribooks.books.book.review.entity.Review;
import shop.nuribooks.books.member.member.entity.Member;

@Builder
public record ReviewBreifResponse(
	long id,
	String title,
	String content,
	int score,
	Member member
) {
	public static ReviewBreifResponse of(Review review) {
		return ReviewBreifResponse.builder()
			.id(review.getId())
			.title(review.getTitle())
			.content(review.getContent())
			.score(review.getScore())
			.member(review.getMember())
			.build();
	}
}
