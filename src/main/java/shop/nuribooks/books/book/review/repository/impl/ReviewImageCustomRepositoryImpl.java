package shop.nuribooks.books.book.review.repository.impl;

import static shop.nuribooks.books.book.review.entity.QReviewImage.*;

import java.util.List;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.review.dto.ReviewImageDto;
import shop.nuribooks.books.book.review.dto.response.ReviewImageResponse;
import shop.nuribooks.books.book.review.repository.ReviewImageCustomRepository;

@RequiredArgsConstructor
public class ReviewImageCustomRepositoryImpl implements ReviewImageCustomRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<ReviewImageDto> findReviewImagesByReviewIds(List<Long> reviewIds) {
		return queryFactory.select(
				Projections.constructor(
					ReviewImageDto.class,
					reviewImage.review.id,
					Projections.constructor(
						ReviewImageResponse.class,
						reviewImage.id,
						reviewImage.imageUrl
					)
				)
			)
			.from(reviewImage)
			.where(reviewImage.review.id.in(reviewIds))
			.fetch();
	}
}
