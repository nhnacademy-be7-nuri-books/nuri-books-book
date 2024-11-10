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
import shop.nuribooks.books.book.point.exception.PointHistoryNotFoundException;
import shop.nuribooks.books.book.point.repository.PointHistoryRepository;
import shop.nuribooks.books.book.point.service.PointHistoryService;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class PointHistoryServiceImpl implements PointHistoryService {
	private final PointHistoryRepository pointHistoryRepository;
	private final MemberRepository memberRepository;

	/**
	 * 포인트 내역 등록
	 *
	 * @param pointHistoryRequest
	 * @return
	 */
	@Override
	public PointHistory registerPointHistory(PointHistoryRequest pointHistoryRequest) {
		return this.pointHistoryRepository.save(pointHistoryRequest.toEntity());
	}

	/**
	 * 전체 포인트 내역 목록 조회
	 *
	 * @param pageable
	 * @param period
	 * @return
	 */
	@Override
	public List<PointHistoryResponse> getPointHistories(Pageable pageable,
		PointHistoryPeriodRequest period) {
		long memberId = MemberIdContext.getMemberId();
		if (!this.memberRepository.existsById(memberId)) {
			throw new MemberNotFoundException("회원을 찾을 수 없습니다.");
		}
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
	public List<PointHistoryResponse> getEarnedPointHistories(Pageable pageable, PointHistoryPeriodRequest period) {
		long memberId = MemberIdContext.getMemberId();
		if (!this.memberRepository.existsById(memberId)) {
			throw new MemberNotFoundException("회원을 찾을 수 없습니다.");
		}
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
	public List<PointHistoryResponse> getUsedPointHistories(Pageable pageable, PointHistoryPeriodRequest period) {
		long memberId = MemberIdContext.getMemberId();
		if (!this.memberRepository.existsById(memberId)) {
			throw new MemberNotFoundException("회원을 찾을 수 없습니다.");
		}
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
