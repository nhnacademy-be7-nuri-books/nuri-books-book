package shop.nuribooks.books.book.review.event.handler;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.point.dto.request.register.ReviewSavingPointRequest;
import shop.nuribooks.books.book.point.enums.PolicyName;
import shop.nuribooks.books.book.point.service.PointHistoryService;
import shop.nuribooks.books.book.review.entity.Review;
import shop.nuribooks.books.book.review.event.ReviewRegisteredEvent;
import shop.nuribooks.books.member.member.entity.Member;

@Component
@RequiredArgsConstructor
public class ReviewRegisterEventHandler {
	private final PointHistoryService pointHistoryService;

	// point event listener 추가.
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void createPointHistory(ReviewRegisteredEvent event) {
		Member member = event.getMember();
		Review review = event.getReview();
		ReviewSavingPointRequest reviewSavingPointRequest = new ReviewSavingPointRequest(member, review);
		PolicyName policyName = review.getReviewImages().isEmpty() ? PolicyName.REVIEW : PolicyName.IMAGE_REVIEW;
		pointHistoryService.registerPointHistory(reviewSavingPointRequest, policyName);
	}

}
