package shop.nuribooks.books.book.point.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.point.dto.request.PointHistoryPeriodRequest;
import shop.nuribooks.books.book.point.dto.response.PointHistoryResponse;

public interface PointHistoryCustomRepository {
	/**
	 * 활성화된 특정 기간 내의 포인트 내역.
	 * @param pointHistoryPeriodRequest
	 * @param pageable
	 * @param memberId
	 * @return
	 */
	List<PointHistoryResponse> findPointHistories(
		PointHistoryPeriodRequest pointHistoryPeriodRequest,
		Pageable pageable,
		long memberId);

	/**
	 * 적립 포인트 내역
	 * @param pointHistoryPeriodRequest
	 * @param pageable
	 * @param memberId
	 * @return
	 */
	List<PointHistoryResponse> findEarnedPointHistories(
		PointHistoryPeriodRequest pointHistoryPeriodRequest,
		Pageable pageable,
		long memberId);

	/**
	 * 사용 포인트 내역
	 * @param pointHistoryPeriodRequest
	 * @param pageable
	 * @param memberId
	 * @return
	 */
	List<PointHistoryResponse> findUsedPointHistories(
		PointHistoryPeriodRequest pointHistoryPeriodRequest,
		Pageable pageable,
		long memberId);

	/**
	 * 유저의 포인트 내역 전체 개수
	 * @param memberId
	 * @param pointHistoryPeriodRequest
	 * @return
	 */
	long countPointHistories(long memberId, PointHistoryPeriodRequest pointHistoryPeriodRequest);

	/**
	 * 유저의 사용 포인트 내역 전체 개수
	 * @param memberId
	 * @param pointHistoryPeriodRequest
	 * @return
	 */
	long countUsedPointHistories(long memberId, PointHistoryPeriodRequest pointHistoryPeriodRequest);

	/**
	 * 유저의 적립 포인트 내역 전체 개수
	 * @param memberId
	 * @param pointHistoryPeriodRequest
	 * @return
	 */
	long countEarnedPointHistories(long memberId, PointHistoryPeriodRequest pointHistoryPeriodRequest);
}
