package shop.nuribooks.books.book.review.service;

import java.util.List;

import shop.nuribooks.books.book.review.dto.request.ReviewRegisterRequest;
import shop.nuribooks.books.book.review.dto.response.ReviewBookResponse;
import shop.nuribooks.books.book.review.dto.response.ReviewMemberResponse;

public interface ReviewService {
	ReviewMemberResponse registerReview(ReviewRegisterRequest reviewRegisterRequest, long memberId);

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
	List<ReviewMemberResponse> getReviewsWithMember(long bookId);

	/**
	 * 회원과 관련된 review 목록 반환
	 * @param memberId
	 * @return
	 */
	List<ReviewBookResponse> getReviewsWithBook(long memberId);
}
