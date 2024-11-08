package shop.nuribooks.books.book.review.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.review.dto.request.ReviewRequest;
import shop.nuribooks.books.book.review.dto.response.ReviewBookResponse;
import shop.nuribooks.books.book.review.dto.response.ReviewMemberResponse;
import shop.nuribooks.books.common.message.PagedResponse;

public interface ReviewService {
	ReviewMemberResponse registerReview(ReviewRequest reviewRegisterRequest, long ownerId);

	/**
	 * 도서의 평균 평점 반환
	 * @param bookId
	 * @return
	 */
	double getScoreByBookId(long bookId);

	/**
	 * 도서와 관련된 review 목록 반환
	 * @param bookId
	 * @return
	 */
	PagedResponse<ReviewMemberResponse> getReviewsWithMember(long bookId, Pageable pageable);

	/**
	 * 회원과 관련된 review 목록 반환
	 *
	 * @param memberId
	 * @return
	 */
	List<ReviewBookResponse> getReviewsWithBook(long memberId, Pageable pageable);

	ReviewMemberResponse updateReview(ReviewRequest reviewRequest, long reviewId, long ownerId);
}
