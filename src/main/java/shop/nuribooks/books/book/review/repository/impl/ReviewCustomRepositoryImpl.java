package shop.nuribooks.books.book.review.repository.impl;

import static com.querydsl.core.group.GroupBy.*;
import static shop.nuribooks.books.book.book.entity.QBook.*;
import static shop.nuribooks.books.book.review.entity.QReview.*;
import static shop.nuribooks.books.book.review.entity.QReviewImage.*;
import static shop.nuribooks.books.member.member.entity.QMember.*;

import java.util.List;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.dto.BookBriefResponse;
import shop.nuribooks.books.book.review.dto.response.ReviewBookResponse;
import shop.nuribooks.books.book.review.dto.response.ReviewImageResponse;
import shop.nuribooks.books.book.review.dto.response.ReviewMemberResponse;
import shop.nuribooks.books.book.review.repository.ReviewCustomRepository;
import shop.nuribooks.books.member.member.dto.response.MemberBriefResponse;

@RequiredArgsConstructor
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<ReviewMemberResponse> findReviewsByBookId(long bookId) {
		return queryFactory.from(review)
			.join(review.member, member)
			.join(review.reviewImages, reviewImage)
			.where(review.book.id.eq(bookId))
			.where(review.updateAt.isNull())
			.transform(
				groupBy(review.id).list(
					Projections.constructor(
						ReviewMemberResponse.class,
						review.id,
						review.title,
						review.content,
						review.score,
						Projections.constructor(
							MemberBriefResponse.class,
							member.id,
							member.username
						),
						list(
							Projections.constructor(
								ReviewImageResponse.class,
								reviewImage.id,
								reviewImage.imageUrl
							)
						)
					)
				)
			);
	}

	@Override
	public double findScoreByBookId(long bookId) {
		Double avgScore = queryFactory
			.select(review.score.avg())
			.from(review)
			.where(review.book.id.eq(bookId))
			.where(review.updateAt.isNull())
			.fetchOne();
		return Math.round(avgScore * 100.0) / 100.0;
	}

	@Override
	public List<ReviewBookResponse> findReviewsByMemberId(long memberId) {
		return queryFactory.from(review)
			.join(review.book, book)
			.join(review.reviewImages, reviewImage)
			.where(review.member.id.eq(memberId))
			.where(review.updateAt.isNull())
			.transform(
				groupBy(review.id).list(
					Projections.constructor(
						ReviewBookResponse.class,
						review.id,
						review.title,
						review.content,
						review.score,
						Projections.constructor(
							BookBriefResponse.class,
							book.id,
							book.title,
							book.thumbnailImageUrl
						),
						list(
							Projections.constructor(
								ReviewImageResponse.class,
								reviewImage.id,
								reviewImage.imageUrl
							)
						)
					)
				)
			);
	}
}
