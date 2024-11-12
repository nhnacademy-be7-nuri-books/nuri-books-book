package shop.nuribooks.books.book.point.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
import shop.nuribooks.books.common.message.PagedResponse;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PointHistoryServiceImpl implements PointHistoryService {
	private final PointHistoryRepository pointHistoryRepository;
	private final PointPolicyRepository pointPolicyRepository;
	private final MemberRepository memberRepository;

	/**
	 * 포인트 내역 등록
	 * 포인트 등록하는 법: 정책 이름 찾기. 자신의 타입에 맞는 point history request 찾기. 이 함수에 인자로 넣기. 끝!
	 * 트랜잭션... 유저랑 엮여야하는데..?
	 * @param pointHistoryRequest
	 * @return
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public PointHistory registerPointHistory(PointHistoryRequest pointHistoryRequest, PolicyName policyName) {

		PointPolicy pointPolicy = pointPolicyRepository.findPointPolicyByNameIgnoreCaseAndDeletedAtIsNull(
			policyName.toString()).orElseThrow(() -> new PointPolicyNotFoundException());
		// 멤버에 포인터 변화량 적용.
		PointHistory pointHistory = pointHistoryRequest.toEntity(pointPolicy);
		Member member = pointHistoryRequest.getMember();
		member.setPoint(member.getPoint().add(pointHistory.getAmount()));
		return this.pointHistoryRepository.save(pointHistory);
	}

	/**
	 * 전체 포인트 내역 목록 조회
	 *
	 * @param pageable
	 * @param period
	 * @return
	 */
	@Override
	public PagedResponse<PointHistoryResponse> getPointHistories(long memberId, Pageable pageable,
		PointHistoryPeriodRequest period) {
		List<PointHistoryResponse> result = this.pointHistoryRepository.findPointHistories(period, pageable, memberId);
		int count = (int)this.pointHistoryRepository.countPointHistories(memberId, period);
		PagedResponse<PointHistoryResponse> response = PagedResponse.of(result, pageable, count);
		return response;
	}

	/**
	 * 적립 포인트 내역 목록 조회
	 *
	 * @param pageable
	 * @param period
	 * @return
	 */
	@Override
	public PagedResponse<PointHistoryResponse> getEarnedPointHistories(long memberId, Pageable pageable,
		PointHistoryPeriodRequest period) {
		List<PointHistoryResponse> result = this.pointHistoryRepository.findEarnedPointHistories(period, pageable,
			memberId);
		int count = (int)this.pointHistoryRepository.countEarnedPointHistories(memberId, period);
		PagedResponse<PointHistoryResponse> response = PagedResponse.of(result, pageable, count);
		return response;
	}

	/**
	 * 사용 포인트 내역 목록 조회
	 *
	 * @param pageable
	 * @param period
	 * @return
	 */
	@Override
	public PagedResponse<PointHistoryResponse> getUsedPointHistories(long memberId, Pageable pageable,
		PointHistoryPeriodRequest period) {
		List<PointHistoryResponse> result = this.pointHistoryRepository.findUsedPointHistories(period, pageable,
			memberId);
		int count = (int)this.pointHistoryRepository.countUsedPointHistories(memberId, period);
		PagedResponse<PointHistoryResponse> response = PagedResponse.of(result, pageable, count);
		return response;
	}

	/**
	 * 포인트 내역 삭제
	 *
	 * @param pointHistoryId
	 */
	@Transactional
	@Override
	public void deletePointHistory(long pointHistoryId) {
		PointHistory pointHistory = this.pointHistoryRepository.findById(pointHistoryId)
			.orElseThrow(() -> new PointHistoryNotFoundException());
		pointHistory.setDeletedAt(LocalDateTime.now());
	}
}