package shop.nuribooks.books.book.review.service;

import shop.nuribooks.books.book.review.dto.request.ReviewRequest;
import shop.nuribooks.books.book.review.dto.response.ReviewMemberResponse;

public interface ReviewService {
	ReviewMemberResponse registerReview(ReviewRequest reviewRequest, long memberId);
	// fk id들로 목록 조회
	// 리뷰 id로 조회
}
