package shop.nuribooks.books.book.point.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.point.dto.request.PointHistoryPeriodRequest;
import shop.nuribooks.books.book.point.dto.request.register.PointHistoryRequest;
import shop.nuribooks.books.book.point.dto.response.PointHistoryResponse;
import shop.nuribooks.books.book.point.entity.PointHistory;
import shop.nuribooks.books.book.point.entity.PointPolicy;
import shop.nuribooks.books.book.point.enums.PolicyName;
import shop.nuribooks.books.book.point.exception.PointHistoryNotFoundException;
import shop.nuribooks.books.book.point.exception.PointPolicyNotFoundException;
import shop.nuribooks.books.book.point.repository.PointHistoryRepository;
import shop.nuribooks.books.book.point.repository.PointPolicyRepository;
import shop.nuribooks.books.book.point.service.PointHistoryService;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class PointHistoryServiceImpl implements PointHistoryService {
	private final PointHistoryRepository pointHistoryRepository;
	private final PointPolicyRepository pointPolicyRepository;
	private final MemberRepository memberRepository;

	/**
	 * 포인트 내역 등록
	 * 포인트 등록하는 법: 정책 이름 찾기. 자신의 타입에 맞는 point history request 찾기. 이 함수에 인자로 넣기. 끝!
	 * @param pointHistoryRequest
	 * @return
	 */
	@Override
	public PointHistory registerPointHistory(PointHistoryRequest pointHistoryRequest, PolicyName policyName) {
		PointPolicy pointPolicy = pointPolicyRepository.findPointPolicyByNameIgnoreCaseAndDeletedAtIsNull(
			policyName.toString()).orElseThrow(() -> new PointPolicyNotFoundException());
		// 멤버에 포인터 변화량 적용.
		return this.pointHistoryRepository.save(pointHistoryRequest.toEntity(pointPolicy));
	}

	/**
	 * 전체 포인트 내역 목록 조회
	 *
	 * @param pageable
	 * @param period
	 * @return
	 */
	@Override
	public List<PointHistoryResponse> getPointHistories(long memberId, Pageable pageable,
		PointHistoryPeriodRequest period) {
		return this.pointHistoryRepository.findPointHistories(period, pageable, memberId);
	}

	/**
	 * 적립 포인트 내역 목록 조회
	 *
	 * @param pageable
	 * @param period
	 * @return
	 */
	@Override
	public List<PointHistoryResponse> getEarnedPointHistories(long memberId, Pageable pageable,
		PointHistoryPeriodRequest period) {
		return this.pointHistoryRepository.findEarnedPointHistories(period, pageable, memberId);
	}

	/**
	 * 사용 포인트 내역 목록 조회
	 *
	 * @param pageable
	 * @param period
	 * @return
	 */
	@Override
	public List<PointHistoryResponse> getUsedPointHistories(long memberId, Pageable pageable,
		PointHistoryPeriodRequest period) {
		return this.pointHistoryRepository.findUsedPointHistories(period, pageable, memberId);
	}

	/**
	 * 포인트 내역 삭제
	 *
	 * @param pointHistoryId
	 */
	@Override
	public void deletePointHistory(long pointHistoryId) {
		PointHistory pointHistory = this.pointHistoryRepository.findById(pointHistoryId)
			.orElseThrow(() -> new PointHistoryNotFoundException());
		pointHistory.setDeletedAt(LocalDateTime.now());
	}
}
