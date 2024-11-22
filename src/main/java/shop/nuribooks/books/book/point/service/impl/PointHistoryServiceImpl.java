package shop.nuribooks.books.book.point.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.book.point.dto.request.PointHistoryPeriodRequest;
import shop.nuribooks.books.book.point.dto.request.register.PointHistoryRequest;
import shop.nuribooks.books.book.point.dto.response.PointHistoryResponse;
import shop.nuribooks.books.book.point.entity.PointHistory;
import shop.nuribooks.books.book.point.entity.PointPolicy;
import shop.nuribooks.books.book.point.enums.HistoryType;
import shop.nuribooks.books.book.point.enums.PolicyName;
import shop.nuribooks.books.book.point.exception.PointHistoryNotFoundException;
import shop.nuribooks.books.book.point.exception.PointPolicyNotFoundException;
import shop.nuribooks.books.book.point.repository.PointHistoryRepository;
import shop.nuribooks.books.book.point.repository.PointPolicyRepository;
import shop.nuribooks.books.book.point.service.PointHistoryService;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@Slf4j
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
	@Transactional
	public PointHistory registerPointHistory(PointHistoryRequest pointHistoryRequest, PolicyName policyName) {

		PointPolicy pointPolicy = pointPolicyRepository.findPointPolicyByNameIgnoreCaseAndDeletedAtIsNull(
			policyName.toString()).orElseThrow(() -> new PointPolicyNotFoundException());
		// 멤버에 포인터 변화량 적용.
		PointHistory pointHistory = pointHistoryRequest.toEntity(pointPolicy);
		Member member = pointHistoryRequest.getMember();
		member.setPoint(member.getPoint().add(pointHistory.getAmount()));
		return pointHistoryRepository.save(pointHistory);
	}

	/**
	 * 전체 포인트 내역 목록 조회
	 *
	 * @param pageable
	 * @param period
	 * @return
	 */
	@Override
	public Page<PointHistoryResponse> getPointHistories(long memberId, HistoryType type, Pageable pageable,
		PointHistoryPeriodRequest period) {
		List<PointHistoryResponse> result = this.pointHistoryRepository.findPointHistories(period, pageable, memberId,
			type);
		int count = (int)this.pointHistoryRepository.countPointHistories(memberId, period, type);
		Page<PointHistoryResponse> response = new PageImpl(result, pageable, count);
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
