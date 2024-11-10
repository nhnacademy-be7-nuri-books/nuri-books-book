package shop.nuribooks.books.book.point.service;

import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.point.dto.request.PointHistoryPeriodRequest;
import shop.nuribooks.books.book.point.dto.request.register.PointHistoryRequest;
import shop.nuribooks.books.book.point.dto.response.PointHistoryResponse;
import shop.nuribooks.books.book.point.entity.PointHistory;
import shop.nuribooks.books.book.point.enums.PolicyName;
import shop.nuribooks.books.common.message.PagedResponse;

public interface PointHistoryService {
	/**
	 * 포인트 내역 등록
	 * @param pointHistoryRequest
	 * @return
	 */
	PointHistory registerPointHistory(PointHistoryRequest pointHistoryRequest, PolicyName policyName);

	/**
	 * 포인트 내역 목록 조회
	 * @param pageable
	 * @param period
	 * @return
	 */
	PagedResponse<PointHistoryResponse> getPointHistories(long memberId, Pageable pageable,
		PointHistoryPeriodRequest period);

	/**
	 * 적립 포인트 내역 목록 조회
	 * @param pageable
	 * @param period
	 * @return
	 */
	PagedResponse<PointHistoryResponse> getEarnedPointHistories(long memberId, Pageable pageable,
		PointHistoryPeriodRequest period);

	/**
	 * 사용 포인트 내역 목록 조회
	 * @param pageable
	 * @param period
	 * @return
	 */
	PagedResponse<PointHistoryResponse> getUsedPointHistories(long memberId, Pageable pageable,
		PointHistoryPeriodRequest period);

	/**
	 * 포인트 내역 삭제
	 * @param pointHistoryId
	 */
	void deletePointHistory(long pointHistoryId);
}
