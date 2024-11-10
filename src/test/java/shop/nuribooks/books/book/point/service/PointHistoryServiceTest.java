package shop.nuribooks.books.book.point.service;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import shop.nuribooks.books.book.point.dto.request.PointHistoryPeriodRequest;
import shop.nuribooks.books.book.point.dto.request.PointPolicyRequest;
import shop.nuribooks.books.book.point.dto.request.register.PointHistoryRequest;
import shop.nuribooks.books.book.point.dto.request.register.ReviewSavingPointRequest;
import shop.nuribooks.books.book.point.dto.response.PointHistoryResponse;
import shop.nuribooks.books.book.point.entity.PointHistory;
import shop.nuribooks.books.book.point.entity.PointPolicy;
import shop.nuribooks.books.book.point.entity.child.ReviewSavingPoint;
import shop.nuribooks.books.book.point.enums.PolicyType;
import shop.nuribooks.books.book.point.exception.PointHistoryNotFoundException;
import shop.nuribooks.books.book.point.repository.PointHistoryRepository;
import shop.nuribooks.books.book.point.service.impl.PointHistoryServiceImpl;
import shop.nuribooks.books.book.review.entity.Review;
import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
public class PointHistoryServiceTest {
	@InjectMocks
	private PointHistoryServiceImpl pointHistoryService;

	@Mock
	private PointHistoryRepository pointHistoryRepository;
	@Mock
	private MemberRepository memberRepository;

	private Member member;

	private PointHistory pointHistory;
	private List<PointHistoryResponse> pointHistoryResponse = new LinkedList<>();
	private PointHistoryRequest pointHistoryRequest;

	@BeforeEach
	public void setUp() {
		member = Member.builder().build();
		TestUtils.setIdForEntity(member, 1l);
		MemberIdContext.setMemberId(1l);

		PointPolicyRequest pointPolicyRequest = new PointPolicyRequest(PolicyType.FIXED, "임시", BigDecimal.valueOf(100));
		PointPolicy pointPolicy = pointPolicyRequest.toEntity();
		this.pointHistoryRequest = new PointHistoryRequest(member, pointPolicy);
		this.pointHistory = this.pointHistoryRequest.toEntity();
		TestUtils.setIdForEntity(pointHistory, 1l);

		this.pointHistoryResponse.add(
			new PointHistoryResponse(1l, BigDecimal.TEN, "", LocalDateTime.now())
		);
	}

	@Test
	public void registerPointHistory() {
		when(pointHistoryRepository.save(any())).thenReturn(pointHistory);
		PointHistory result = this.pointHistoryService.registerPointHistory(pointHistoryRequest);
		assertEquals(pointHistory, result);
	}

	@Test
	public void registerReviewSavingPoint() {
		PointPolicyRequest pointPolicyRequest = new PointPolicyRequest(PolicyType.FIXED, "리뷰", BigDecimal.valueOf(500));
		PointPolicy pointPolicy = pointPolicyRequest.toEntity();
		ReviewSavingPointRequest reviewSavingPointRequest = new ReviewSavingPointRequest(member, pointPolicy,
			Review.builder().build());
		ReviewSavingPoint reviewSavingPoint = reviewSavingPointRequest.toEntity();
		when(pointHistoryRepository.save(any())).thenReturn(reviewSavingPoint);
		PointHistory result = this.pointHistoryService.registerPointHistory(pointHistoryRequest);
		assertEquals(reviewSavingPoint, result);
	}

	@Test
	public void getPointHistories() {
		when(memberRepository.existsById(anyLong())).thenReturn(true);
		when(pointHistoryRepository.findPointHistories(any(), any(), anyLong())).thenReturn(this.pointHistoryResponse);
		assertEquals(1,
			pointHistoryService.getPointHistories(PageRequest.of(0, 1), new PointHistoryPeriodRequest()).size());
	}

	@Test
	public void getPointHistoriesFail() {
		when(memberRepository.existsById(anyLong())).thenReturn(false);
		assertThrows(MemberNotFoundException.class,
			() -> pointHistoryService.getPointHistories(PageRequest.of(0, 1), new PointHistoryPeriodRequest()).size());
	}

	@Test
	public void getEarnedPointHistories() {
		when(memberRepository.existsById(anyLong())).thenReturn(true);
		when(pointHistoryRepository.findEarnedPointHistories(any(), any(), anyLong())).thenReturn(
			this.pointHistoryResponse);
		assertEquals(1,
			pointHistoryService.getEarnedPointHistories(PageRequest.of(0, 1), new PointHistoryPeriodRequest()).size());
	}

	@Test
	public void getEarnedPointHistoriesFail() {
		when(memberRepository.existsById(anyLong())).thenReturn(false);
		assertThrows(MemberNotFoundException.class,
			() -> pointHistoryService.getEarnedPointHistories(PageRequest.of(0, 1), new PointHistoryPeriodRequest()));
	}

	@Test
	public void getUsedPointHistories() {
		when(memberRepository.existsById(anyLong())).thenReturn(true);
		when(pointHistoryRepository.findUsedPointHistories(any(), any(), anyLong())).thenReturn(
			this.pointHistoryResponse);
		assertEquals(1,
			pointHistoryService.getUsedPointHistories(PageRequest.of(0, 1), new PointHistoryPeriodRequest()).size());
	}

	@Test
	public void getUsedPointHistoriesFail() {
		when(memberRepository.existsById(anyLong())).thenReturn(false);
		assertThrows(MemberNotFoundException.class,
			() -> pointHistoryService.getUsedPointHistories(PageRequest.of(0, 1), new PointHistoryPeriodRequest()));
	}

	@Test
	public void deletePointHistorySuccess() {
		when(pointHistoryRepository.findById(anyLong())).thenReturn(Optional.of(pointHistory));
		pointHistoryService.deletePointHistory(1);
		assert (pointHistory.getDeletedAt() != null);
	}

	@Test
	public void deletePointHistoryFail() {
		when(pointHistoryRepository.findById(anyLong())).thenReturn(Optional.empty());
		assertThrows(PointHistoryNotFoundException.class, () -> pointHistoryService.deletePointHistory(1));
	}
}
