package shop.nuribooks.books.book.review.repository.impl;

import static com.querydsl.core.group.GroupBy.*;
import static shop.nuribooks.books.book.book.entity.QBook.*;
import static shop.nuribooks.books.book.review.entity.QReview.*;
import static shop.nuribooks.books.member.member.entity.QMember.*;

import java.util.LinkedList;
import java.util.List;

import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.dto.BookBriefResponse;
import shop.nuribooks.books.book.review.dto.response.ReviewBookResponse;
import shop.nuribooks.books.book.review.dto.response.ReviewMemberResponse;
import shop.nuribooks.books.book.review.repository.ReviewCustomRepository;
import shop.nuribooks.books.member.member.dto.response.MemberBriefResponse;

@RequiredArgsConstructor
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<ReviewMemberResponse> findReviewsByBookId(long bookId, Pageable pageable) {
		return queryFactory.selectFrom(review)
			.where(review.book.id.eq(bookId))
			.join(review.member, member)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			// .orderBy(pageable.getSort().get)
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
						Projections.constructor(
							LinkedList.class
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
			.fetchOne();
		return Math.round(avgScore * 100.0) / 100.0;
	}

	@Override
	public List<ReviewBookResponse> findReviewsByMemberId(long memberId, Pageable pageable) {
		return queryFactory.from(review)
			.join(review.book, book)
			.where(review.member.id.eq(memberId))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
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
						Projections.constructor(
							LinkedList.class
						)
					)
				)
			);
	}

	@Override
	public long countByBookId(long bookId) {
		return queryFactory.select(review.count())
			.from(review)
			.where(review.book.id.eq(bookId))
			.fetchOne();
	}

	@Override
	public long countByMemberId(long memberId) {
		return queryFactory.select(review.count())
			.from(review)
			.where(review.member.id.eq(memberId))
			.fetchOne();
	}
}
