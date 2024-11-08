package shop.nuribooks.books.book.review.repository;

import java.util.List;

import com.querydsl.core.Tuple;

public interface ReviewImageCustomRepository {
	List<Tuple> findReviewImagesByReviewIds(List<Long> reviewIds);
}
