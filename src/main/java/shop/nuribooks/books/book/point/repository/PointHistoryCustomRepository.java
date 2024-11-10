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
}
