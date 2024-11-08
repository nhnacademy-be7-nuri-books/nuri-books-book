package shop.nuribooks.books.book.review.repository.impl;

import static com.querydsl.core.group.GroupBy.*;
import static shop.nuribooks.books.book.review.entity.QReviewImage.*;

import java.util.List;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.review.dto.response.ReviewImageResponse;
import shop.nuribooks.books.book.review.repository.ReviewImageCustomRepository;

@RequiredArgsConstructor
public class ReviewImageCustomRepositoryImpl implements ReviewImageCustomRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<ReviewImageResponse> findReviewImagesByReviewIds(List<Long> reviewIds) {
		return queryFactory.from(reviewImage)
			.where(reviewImage.review.id.in(reviewIds))
			.transform(
				groupBy(reviewImage.review.id).list(
					Projections.constructor(
						ReviewImageResponse.class,
						reviewImage.id,
						reviewImage.imageUrl
					)
				)
			);
	}
}
