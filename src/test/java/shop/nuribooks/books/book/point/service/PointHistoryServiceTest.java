package shop.nuribooks.books.book.point.service;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import shop.nuribooks.books.book.point.dto.request.PointHistoryPeriodRequest;
import shop.nuribooks.books.book.point.dto.request.PointPolicyRequest;
import shop.nuribooks.books.book.point.dto.request.register.OrderCancelReturningPointRequest;
import shop.nuribooks.books.book.point.dto.request.register.OrderSavingPointRequest;
import shop.nuribooks.books.book.point.dto.request.register.OrderUsingPointRequest;
import shop.nuribooks.books.book.point.dto.request.register.PointHistoryRequest;
import shop.nuribooks.books.book.point.dto.request.register.RefundReturningPointRequest;
import shop.nuribooks.books.book.point.dto.request.register.ReviewSavingPointRequest;
import shop.nuribooks.books.book.point.dto.response.PointHistoryResponse;
import shop.nuribooks.books.book.point.entity.PointHistory;
import shop.nuribooks.books.book.point.entity.PointPolicy;
import shop.nuribooks.books.book.point.entity.child.OrderCancelReturningPoint;
import shop.nuribooks.books.book.point.entity.child.OrderSavingPoint;
import shop.nuribooks.books.book.point.entity.child.OrderUsingPoint;
import shop.nuribooks.books.book.point.entity.child.RefundReturningPoint;
import shop.nuribooks.books.book.point.entity.child.ReviewSavingPoint;
import shop.nuribooks.books.book.point.enums.HistoryType;
import shop.nuribooks.books.book.point.enums.PolicyName;
import shop.nuribooks.books.book.point.enums.PolicyType;
import shop.nuribooks.books.book.point.exception.PointHistoryNotFoundException;
import shop.nuribooks.books.book.point.exception.PointOverException;
import shop.nuribooks.books.book.point.exception.PointPolicyNotFoundException;
import shop.nuribooks.books.book.point.repository.PointHistoryRepository;
import shop.nuribooks.books.book.point.repository.PointPolicyRepository;
import shop.nuribooks.books.book.point.service.impl.PointHistoryServiceImpl;
import shop.nuribooks.books.book.review.entity.Review;
import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.refund.dto.request.RefundRequest;
import shop.nuribooks.books.order.refund.entity.Refund;

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
	private Order order;
	private Refund refund;

	private PointHistory pointHistory;
	private List<PointHistoryResponse> pointHistoryResponse = new LinkedList<>();
	private PointHistoryRequest pointHistoryRequest;
	private PointPolicy pointPolicy;

	@BeforeEach
	void setUp() {
		member = Member.builder().point(BigDecimal.valueOf(500)).grade(TestUtils.creategrade()).build();
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

		order = TestUtils.createOrder(member.getCustomer());
		RefundRequest refundRequest = new RefundRequest(BigDecimal.valueOf(500), "그냥");
		refund = refundRequest.toEntity(order);
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
		Assertions.assertEquals(reviewSavingPoint, result);
	}

	@Test
	void registerOrderUsingPoint() {
		PointPolicyRequest pointPolicyRequest = new PointPolicyRequest(PolicyType.FIXED, "사용", BigDecimal.valueOf(500));
		pointPolicy = pointPolicyRequest.toEntity();
		OrderUsingPointRequest orderUsingPointRequest = new OrderUsingPointRequest(member, order,
			BigDecimal.valueOf(500));
		OrderUsingPoint orderUsingPoint = orderUsingPointRequest.toEntity(pointPolicy);

		when(pointHistoryRepository.save(any())).thenReturn(orderUsingPoint);
		when(pointPolicyRepository.findPointPolicyByNameIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(
			Optional.of(pointPolicy));
		OrderUsingPoint result = (OrderUsingPoint)this.pointHistoryService.registerPointHistory(orderUsingPointRequest,
			PolicyName.USING);
		Assertions.assertEquals(orderUsingPoint, result);
	}

	@Test
	void registerOrderUsingPointFailed() {
		member.setPoint(BigDecimal.ZERO);
		assertThrows(PointOverException.class, () -> new OrderUsingPointRequest(member, order,
			BigDecimal.valueOf(500)));
	}

	@Test
	void registerOrderCancelPoint() {
		PointPolicyRequest pointPolicyRequest = new PointPolicyRequest(PolicyType.FIXED, "취소", BigDecimal.valueOf(500));
		pointPolicy = pointPolicyRequest.toEntity();
		OrderCancelReturningPointRequest orderCancelReturningPointRequest = new OrderCancelReturningPointRequest(member,
			order,
			BigDecimal.valueOf(500));
		OrderCancelReturningPoint orderCancelReturningPoint = orderCancelReturningPointRequest.toEntity(pointPolicy);

		when(pointHistoryRepository.save(any())).thenReturn(orderCancelReturningPoint);
		when(pointPolicyRepository.findPointPolicyByNameIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(
			Optional.of(pointPolicy));
		OrderCancelReturningPoint result = (OrderCancelReturningPoint)this.pointHistoryService.registerPointHistory(
			orderCancelReturningPointRequest,
			PolicyName.USING);
		Assertions.assertEquals(orderCancelReturningPoint, result);
	}

	@Test
	void registerOrderSavingPoint() {
		PointPolicyRequest pointPolicyRequest = new PointPolicyRequest(PolicyType.FIXED, "적립", BigDecimal.valueOf(500));
		pointPolicy = pointPolicyRequest.toEntity();
		OrderSavingPointRequest orderSavingPointRequest = new OrderSavingPointRequest(member, order,
			BigDecimal.valueOf(500));
		OrderSavingPoint orderSavingPoint = orderSavingPointRequest.toEntity(pointPolicy);

		when(pointHistoryRepository.save(any())).thenReturn(orderSavingPoint);
		when(pointPolicyRepository.findPointPolicyByNameIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(
			Optional.of(pointPolicy));
		OrderSavingPoint result = (OrderSavingPoint)this.pointHistoryService.registerPointHistory(
			orderSavingPointRequest,
			PolicyName.USING);
		Assertions.assertEquals(orderSavingPoint, result);
	}

	@Test
	void registerRefundReturningPoint() {
		PointPolicyRequest pointPolicyRequest = new PointPolicyRequest(PolicyType.FIXED, "환불", BigDecimal.valueOf(500));
		pointPolicy = pointPolicyRequest.toEntity();
		RefundReturningPointRequest refundReturningPointRequest = new RefundReturningPointRequest(member, refund,
			BigDecimal.valueOf(500));
		RefundReturningPoint refundReturningPoint = refundReturningPointRequest.toEntity(pointPolicy);

		when(pointHistoryRepository.save(any())).thenReturn(refundReturningPoint);
		when(pointPolicyRepository.findPointPolicyByNameIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(
			Optional.of(pointPolicy));
		RefundReturningPoint result = (RefundReturningPoint)this.pointHistoryService.registerPointHistory(
			refundReturningPointRequest,
			PolicyName.USING);
		Assertions.assertEquals(refundReturningPoint, result);
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
