package shop.nuribooks.books.book.point.dto.request.register;

import jakarta.validation.constraints.NotNull;
import shop.nuribooks.books.book.point.entity.PointPolicy;
import shop.nuribooks.books.book.point.entity.child.ReviewSavingPoint;
import shop.nuribooks.books.book.review.entity.Review;
import shop.nuribooks.books.member.member.entity.Member;

public class ReviewSavingPointRequest extends PointHistoryRequest {
	@NotNull
	Review review;

	public ReviewSavingPointRequest(Member member, Review review) {
		super(member);
		this.review = review;
	}

	public ReviewSavingPoint toEntity(PointPolicy pointPolicy) {
		return ReviewSavingPoint.builder()
			.amount(pointPolicy.getAmount())
			.description(pointPolicy.getName())
			.member(member)
			.pointPolicy(pointPolicy)
			.review(review)
			.build();
	}
}
