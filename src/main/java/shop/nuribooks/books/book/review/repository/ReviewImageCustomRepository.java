package shop.nuribooks.books.book.review.repository;

import java.util.List;

import shop.nuribooks.books.book.review.dto.response.ReviewImageResponse;

public interface ReviewImageCustomRepository {
	List<ReviewImageResponse> findReviewImagesByReviewIds(List<Long> reviewIds);
}
