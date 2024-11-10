package shop.nuribooks.books.book.point.repository.impl;

import static shop.nuribooks.books.book.point.entity.QPointHistory.*;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.point.dto.request.PointHistoryPeriodRequest;
import shop.nuribooks.books.book.point.dto.response.PointHistoryResponse;
import shop.nuribooks.books.book.point.repository.PointHistoryCustomRepository;

@RequiredArgsConstructor
public class PointHistoryCustomRepositoryImpl implements PointHistoryCustomRepository {
	private final JPAQueryFactory queryFactory;

	/**
	 * 활성화된 특정 기간 내의 포인트 내역.
	 *
	 * @param pointHistoryPeriodRequest
	 * @param pageable
	 * @param memberId
	 * @return
	 */
	@Override
	public List<PointHistoryResponse> findPointHistories(PointHistoryPeriodRequest pointHistoryPeriodRequest,
		Pageable pageable, long memberId) {
		return queryFactory.select(
				Projections.constructor(
					PointHistoryResponse.class,
					pointHistory.id,
					pointHistory.amount,
					pointHistory.description,
					pointHistory.createdAt
				)
			).from(pointHistory)
			.where(pointHistory.createdAt.between(pointHistoryPeriodRequest.getStart(),
				pointHistoryPeriodRequest.getEnd()))
			.where(pointHistory.deletedAt.isNull())
			.where(pointHistory.member.id.eq(memberId))
			.orderBy(pointHistory.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	/**
	 * 적립 포인트 내역
	 *
	 * @param pointHistoryPeriodRequest
	 * @param pageable
	 * @param memberId
	 * @return
	 */
	@Override
	public List<PointHistoryResponse> findEarnedPointHistories(PointHistoryPeriodRequest pointHistoryPeriodRequest,
		Pageable pageable, long memberId) {
		return queryFactory.select(
				Projections.constructor(
					PointHistoryResponse.class,
					pointHistory.id,
					pointHistory.amount,
					pointHistory.description,
					pointHistory.createdAt
				)
			).from(pointHistory)
			.where(pointHistory.createdAt.between(pointHistoryPeriodRequest.getStart(),
				pointHistoryPeriodRequest.getEnd()))
			.where(pointHistory.deletedAt.isNull())
			.where(pointHistory.member.id.eq(memberId))
			.where(pointHistory.amount.goe(BigDecimal.ZERO))
			.orderBy(pointHistory.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	/**
	 * 사용 포인트 내역
	 *
	 * @param pointHistoryPeriodRequest
	 * @param pageable
	 * @param memberId
	 * @return
	 */
	@Override
	public List<PointHistoryResponse> findUsedPointHistories(PointHistoryPeriodRequest pointHistoryPeriodRequest,
		Pageable pageable, long memberId) {
		return queryFactory.select(
				Projections.constructor(
					PointHistoryResponse.class,
					pointHistory.id,
					pointHistory.amount,
					pointHistory.description,
					pointHistory.createdAt
				)
			).from(pointHistory)
			.where(pointHistory.createdAt.between(pointHistoryPeriodRequest.getStart(),
				pointHistoryPeriodRequest.getEnd()))
			.where(pointHistory.deletedAt.isNull())
			.where(pointHistory.member.id.eq(memberId))
			.where(pointHistory.amount.lt(BigDecimal.ZERO))
			.orderBy(pointHistory.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}
}
