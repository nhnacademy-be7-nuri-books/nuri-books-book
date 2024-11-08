package shop.nuribooks.books.book.review.repository;

import java.util.List;

import shop.nuribooks.books.book.review.dto.ReviewImageDto;

public interface ReviewImageCustomRepository {
	List<ReviewImageDto> findReviewImagesByReviewIds(List<Long> reviewIds);
}
