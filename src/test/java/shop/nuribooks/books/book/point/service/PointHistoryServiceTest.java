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
import shop.nuribooks.books.book.point.enums.HistoryType;
import shop.nuribooks.books.book.point.enums.PolicyName;
import shop.nuribooks.books.book.point.enums.PolicyType;
import shop.nuribooks.books.book.point.exception.PointHistoryNotFoundException;
import shop.nuribooks.books.book.point.exception.PointPolicyNotFoundException;
import shop.nuribooks.books.book.point.repository.PointHistoryRepository;
import shop.nuribooks.books.book.point.repository.PointPolicyRepository;
import shop.nuribooks.books.book.point.service.impl.PointHistoryServiceImpl;
import shop.nuribooks.books.book.review.entity.Review;
import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class PointHistoryServiceTest {
	@InjectMocks
	private PointHistoryServiceImpl pointHistoryService;

	@Mock
	private PointHistoryRepository pointHistoryRepository;

	@Mock
	private PointPolicyRepository pointPolicyRepository;

	@Mock
	private MemberRepository memberRepository;

	private Member member;

	private PointHistory pointHistory;
	private List<PointHistoryResponse> pointHistoryResponse = new LinkedList<>();
	private PointHistoryRequest pointHistoryRequest;
	private PointPolicy pointPolicy;

	@BeforeEach
	void setUp() {
		member = Member.builder().point(BigDecimal.valueOf(500)).build();
		TestUtils.setIdForEntity(member, 1l);
		MemberIdContext.setMemberId(1l);

		PointPolicyRequest pointPolicyRequest = new PointPolicyRequest(PolicyType.FIXED, "임시", BigDecimal.valueOf(100));
		pointPolicy = pointPolicyRequest.toEntity();
		this.pointHistoryRequest = new PointHistoryRequest(member);
		this.pointHistory = this.pointHistoryRequest.toEntity(pointPolicy);
		TestUtils.setIdForEntity(pointHistory, 1l);

		this.pointHistoryResponse.add(
			new PointHistoryResponse(1l, BigDecimal.TEN, "", LocalDateTime.now())
		);
	}

	@Test
	void registerPointHistory() {
		when(pointHistoryRepository.save(any())).thenReturn(pointHistory);
		when(pointPolicyRepository.findPointPolicyByNameIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(
			Optional.of(pointPolicy));
		PointHistory result = this.pointHistoryService.registerPointHistory(pointHistoryRequest, PolicyName.WELCOME);
		assertEquals(pointHistory, result);
	}

	@Test
	void registerPointHistoryFail() {
		when(pointPolicyRepository.findPointPolicyByNameIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(
			Optional.empty());
		assertThrows(PointPolicyNotFoundException.class,
			() -> this.pointHistoryService.registerPointHistory(pointHistoryRequest, PolicyName.WELCOME));
	}

	@Test
	void registerReviewSavingPoint() {
		PointPolicyRequest pointPolicyRequest = new PointPolicyRequest(PolicyType.FIXED, "리뷰", BigDecimal.valueOf(500));
		pointPolicy = pointPolicyRequest.toEntity();
		ReviewSavingPointRequest reviewSavingPointRequest = new ReviewSavingPointRequest(member,
			Review.builder().build());
		ReviewSavingPoint reviewSavingPoint = reviewSavingPointRequest.toEntity(pointPolicy);

		when(pointHistoryRepository.save(any())).thenReturn(reviewSavingPoint);
		when(pointPolicyRepository.findPointPolicyByNameIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(
			Optional.of(pointPolicy));
		ReviewSavingPoint result = (ReviewSavingPoint)this.pointHistoryService.registerPointHistory(pointHistoryRequest,
			PolicyName.REVIEW);
		assertEquals(reviewSavingPoint, result);
	}

	@Test
	void registerReviewSavingPointFail() {
		when(pointPolicyRepository.findPointPolicyByNameIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(
			Optional.empty());
		assertThrows(PointPolicyNotFoundException.class,
			() -> this.pointHistoryService.registerPointHistory(pointHistoryRequest, PolicyName.REVIEW));
	}

	@Test
	void getPointHistories() {
		when(pointHistoryRepository.findPointHistories(any(), any(), anyLong(), any())).thenReturn(
			this.pointHistoryResponse);
		assertEquals(1,
			pointHistoryService.getPointHistories(1l, HistoryType.ALL, PageRequest.of(0, 1),
					new PointHistoryPeriodRequest())
				.getContent()
				.size());
	}

	@Test
	void getEarnedPointHistories() {
		when(pointHistoryRepository.findPointHistories(any(), any(), anyLong(), any())).thenReturn(
			this.pointHistoryResponse);
		assertEquals(1,
			pointHistoryService.getPointHistories(1l, HistoryType.SAVED, PageRequest.of(0, 1),
					new PointHistoryPeriodRequest())
				.getContent().size());
	}

	@Test
	void getUsedPointHistories() {
		when(pointHistoryRepository.findPointHistories(any(), any(), anyLong(), any())).thenReturn(
			this.pointHistoryResponse);
		assertEquals(1,
			pointHistoryService.getPointHistories(1l, HistoryType.USED, PageRequest.of(0, 1),
					new PointHistoryPeriodRequest())
				.getContent().size());
	}

	@Test
	void deletePointHistorySuccess() {
		when(pointHistoryRepository.findById(anyLong())).thenReturn(Optional.of(pointHistory));
		pointHistoryService.deletePointHistory(1);
		assert (pointHistory.getDeletedAt() != null);
	}

	@Test
	void deletePointHistoryFail() {
		when(pointHistoryRepository.findById(anyLong())).thenReturn(Optional.empty());
		assertThrows(PointHistoryNotFoundException.class, () -> pointHistoryService.deletePointHistory(1));
	}
}
