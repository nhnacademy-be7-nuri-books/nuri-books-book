package shop.nuribooks.books.book.review.dto.response;

import java.util.List;

import lombok.Builder;
import shop.nuribooks.books.book.review.entity.Review;
import shop.nuribooks.books.member.member.dto.response.MemberBriefResponse;

/**
 * 작성자 정보를 함께 담은 review dto
 * @param id
 * @param title
 * @param content
 * @param score
 * @param member
 */
@Builder
public record ReviewMemberResponse(
	long id,
	String title,
	String content,
	int score,
	MemberBriefResponse member,
	List<ReviewImageResponse> reviewImages
) {
	public static ReviewMemberResponse of(Review review) {
		ReviewMemberResponse response = ReviewMemberResponse.builder()
			.id(review.getId())
			.title(review.getTitle())
			.content(review.getContent())
			.score(review.getScore())
			.member(MemberBriefResponse.of(review.getMember()))
			.build();
		return response;
	}
}
