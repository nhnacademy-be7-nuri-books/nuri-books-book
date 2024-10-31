package shop.nuribooks.books.book.review.service;

import shop.nuribooks.books.book.review.dto.request.ReviewRegisterRequest;
import shop.nuribooks.books.book.review.dto.response.ReviewBreifResponse;

public interface ReviewService {
	ReviewBreifResponse registerReview(ReviewRegisterRequest reviewRegisterRequest, long memberId);
	// fk id들로 목록 조회
	// 리뷰 id로 조회
}
