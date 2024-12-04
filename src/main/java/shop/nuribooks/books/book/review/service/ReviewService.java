package shop.nuribooks.books.book.review.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.review.dto.request.ReviewRequest;
import shop.nuribooks.books.book.review.dto.request.ReviewUpdateRequest;
import shop.nuribooks.books.book.review.dto.response.ReviewBookResponse;
import shop.nuribooks.books.book.review.dto.response.ReviewMemberResponse;
import shop.nuribooks.books.book.review.dto.response.ReviewScoreResponse;

public interface ReviewService {
	ReviewMemberResponse registerReview(ReviewRequest reviewRegisterRequest);

	/**
	 * 도서의 평균 평점 반환
	 * @param bookId
	 * @return
	 */
	ReviewScoreResponse getScoreByBookId(long bookId);

	/**
	 * 도서와 관련된 review 목록 반환
	 * @param bookId
	 * @return
	 */
	Page<ReviewMemberResponse> getReviewsByBookId(long bookId, Pageable pageable);

	/**
	 * 회원과 관련된 review 목록 반환
	 *
	 * @param memberId
	 * @return
	 */
	Page<ReviewBookResponse> getReviewsByMemberId(long memberId, Pageable pageable);

	ReviewMemberResponse updateReview(ReviewUpdateRequest reviewUpdateRequest, long reviewId);
}
