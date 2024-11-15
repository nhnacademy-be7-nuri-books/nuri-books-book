package shop.nuribooks.books.book.point.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.point.dto.request.PointHistoryPeriodRequest;
import shop.nuribooks.books.book.point.dto.response.PointHistoryResponse;
import shop.nuribooks.books.book.point.enums.HistoryType;

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
		long memberId,
		HistoryType type);

	/**
	 * 유저의 포인트 내역 전체 개수
	 * @param memberId
	 * @param pointHistoryPeriodRequest
	 * @return
	 */
	long countPointHistories(long memberId, PointHistoryPeriodRequest pointHistoryPeriodRequest, HistoryType type);

}
