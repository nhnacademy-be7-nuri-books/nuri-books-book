package shop.nuribooks.books.book.review.repository.impl;

import static shop.nuribooks.books.book.review.entity.QReviewImage.*;

import java.util.List;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.review.dto.response.ReviewImageResponse;
import shop.nuribooks.books.book.review.repository.ReviewImageCustomRepository;

@RequiredArgsConstructor
public class ReviewImageCustomRepositoryImpl implements ReviewImageCustomRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<Tuple> findReviewImagesByReviewIds(List<Long> reviewIds) {
		return queryFactory.select(
				reviewImage.review.id,
				Projections.constructor(
					ReviewImageResponse.class,
					reviewImage.id,
					reviewImage.imageUrl
				)
			)
			.from(reviewImage)
			.where(reviewImage.review.id.in(reviewIds))
			.fetch();
	}
}
