package shop.nuribooks.books.book.point.dto.request.register;

import jakarta.validation.constraints.NotNull;
import shop.nuribooks.books.book.point.entity.PointPolicy;
import shop.nuribooks.books.book.point.entity.child.ReviewSavingPoint;
import shop.nuribooks.books.book.review.entity.Review;
import shop.nuribooks.books.member.member.entity.Member;

public record ReviewSavingPointRequest(
	@NotNull
	Member member,
	@NotNull
	PointPolicy pointPolicy,
	@NotNull
	Review review
) {
	public ReviewSavingPoint toEntity() {
		ReviewSavingPoint reviewSavingPoint = ReviewSavingPoint.builder()
			.amount(pointPolicy.getAmount())
			.description(pointPolicy.getName())
			.member(member)
			.pointPolicy(pointPolicy)
			.build();

		return reviewSavingPoint;
	}
}
