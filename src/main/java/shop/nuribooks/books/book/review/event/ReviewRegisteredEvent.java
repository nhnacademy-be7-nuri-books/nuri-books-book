package shop.nuribooks.books.book.review.event;

import lombok.AllArgsConstructor;
import shop.nuribooks.books.book.review.entity.Review;
import shop.nuribooks.books.member.member.entity.Member;

@AllArgsConstructor
public class ReviewRegisteredEvent {
	private Member member;
	private Review review;

	public Member getMember() {
		return this.member;
	}

	public Review getReview() {
		return this.review;
	}
}
