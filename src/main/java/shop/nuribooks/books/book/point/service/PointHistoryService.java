package shop.nuribooks.books.book.point.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.point.dto.request.PointHistoryPeriodRequest;
import shop.nuribooks.books.book.point.dto.request.register.PointHistoryRequest;
import shop.nuribooks.books.book.point.dto.response.PointHistoryResponse;
import shop.nuribooks.books.book.point.entity.PointHistory;

public interface PointHistoryService {
	/**
	 * 포인트 내역 등록
	 * @param pointHistoryRequest
	 * @return
	 */
	PointHistory registerPointHistory(PointHistoryRequest pointHistoryRequest);

	/**
	 * 포인트 내역 목록 조회
	 * @param pageable
	 * @param period
	 * @return
	 */
	List<PointHistoryResponse> getPointHistories(Pageable pageable,
		PointHistoryPeriodRequest period);

	/**
	 * 적립 포인트 내역 목록 조회
	 * @param pageable
	 * @param period
	 * @return
	 */
	List<PointHistoryResponse> getEarnedPointHistories(Pageable pageable,
		PointHistoryPeriodRequest period);

	/**
	 * 사용 포인트 내역 목록 조회
	 * @param pageable
	 * @param period
	 * @return
	 */
	List<PointHistoryResponse> getUsedPointHistories(Pageable pageable,
		PointHistoryPeriodRequest period);

	/**
	 * 포인트 내역 삭제
	 * @param pointHistoryId
	 */
	void deletePointHistory(long pointHistoryId);
}
