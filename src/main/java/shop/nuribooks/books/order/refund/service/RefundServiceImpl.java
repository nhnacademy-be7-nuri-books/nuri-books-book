package shop.nuribooks.books.order.refund.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.point.dto.request.register.RefundReturningPointRequest;
import shop.nuribooks.books.book.point.enums.PolicyName;
import shop.nuribooks.books.book.point.service.PointHistoryService;
import shop.nuribooks.books.exception.order.CustomerRefundException;
import shop.nuribooks.books.exception.order.OrderNotFoundException;
import shop.nuribooks.books.exception.refund.RefundNotAllowedException;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.order.repository.OrderRepository;
import shop.nuribooks.books.order.orderdetail.entity.OrderDetail;
import shop.nuribooks.books.order.orderdetail.entity.OrderState;
import shop.nuribooks.books.order.orderdetail.repository.OrderDetailRepository;
import shop.nuribooks.books.order.refund.dto.RefundInfoDto;
import shop.nuribooks.books.order.refund.dto.request.RefundRequest;
import shop.nuribooks.books.order.refund.dto.response.RefundInfo;
import shop.nuribooks.books.order.refund.dto.response.RefundInfoResponse;
import shop.nuribooks.books.order.refund.dto.response.RefundResponse;
import shop.nuribooks.books.order.refund.entity.Refund;
import shop.nuribooks.books.order.refund.repository.RefundRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RefundServiceImpl implements RefundService {
	public static final int MAXIMUM_REFUND_DAY = 10;
	public static final long REFUND_SHIPPING_PRICE = 2500L;

	private final RefundRepository refundRepository;
	private final OrderDetailRepository orderDetailRepository;
	private final OrderRepository orderRepository;
	private final MemberRepository memberRepository;
	private final PointHistoryService pointHistoryService;

	private static RefundInfo getRefundInfo(RefundInfoDto refundInfoDto) {
		BigDecimal paymentPrice = refundInfoDto.paymentPrice();
		BigDecimal shippingPrice = BigDecimal.valueOf(REFUND_SHIPPING_PRICE);
		BigDecimal savingPointAmount = refundInfoDto.orderSavingPoint();
		BigDecimal deductedAmount = shippingPrice.add(savingPointAmount);

		BigDecimal totalRefundAmount =
			paymentPrice.subtract(deductedAmount).compareTo(BigDecimal.ZERO) > 0 ?
				paymentPrice.subtract(deductedAmount) : BigDecimal.ZERO;

		return new RefundInfo(paymentPrice, shippingPrice, savingPointAmount, deductedAmount,
			totalRefundAmount);
	}

	@Override
	public RefundInfoResponse getRefundInfoResponse(Long orderId) {

		List<OrderDetail> orderDetailList = orderDetailRepository.findAllByOrderId(orderId);
		orderDetailList.forEach(
			orderDetail -> {
				if (orderDetail.getOrderState() != OrderState.COMPLETED) {
					throw new RefundNotAllowedException("주문이 완료되어야 반품 가능합니다");
				}
			}
		);

		RefundInfoDto refundInfoDto = refundRepository.findRefundInfo(orderId);

		if (isAfterMaximumRefundDay(refundInfoDto.shippingAt())) {
			throw new RefundNotAllowedException("출고일로부터 10일 이내 반품 가능합니다");
		}
		RefundInfo refundInfo = getRefundInfo(refundInfoDto);

		return new RefundInfoResponse(refundInfo);
	}

	@Override
	@Transactional
	public RefundResponse refund(Long orderId, RefundRequest refundRequest) {

		// 주문상세 가져오기
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new OrderNotFoundException("주문을 찾을 수 없습니다."));

		List<OrderDetail> orderDetailList = orderDetailRepository.findAllByOrderId(orderId);

		orderDetailList.forEach(orderDetail -> orderDetail.setOrderState(OrderState.RETURNED));

		// 멤버 가져오기
		Member member = getMember(order);

		Refund refund = refundRequest.toEntity(order);
		Refund saved = refundRepository.save(refund);

		RefundReturningPointRequest refundReturningPointRequest = new RefundReturningPointRequest(member,
			saved, saved.getRefundAmount());
		//환불 포인트에 저장
		pointHistoryService.registerPointHistory(refundReturningPointRequest, PolicyName.REFUND);
		return RefundResponse.of(saved);
	}

	private boolean isAfterMaximumRefundDay(LocalDateTime shippingAt) {
		return LocalDateTime.now().isAfter(shippingAt.plusDays(MAXIMUM_REFUND_DAY));
	}

	private Member getMember(Order order) {
		Long customerId = order.getCustomer().getId();
		return memberRepository.findById(customerId)
			.orElseThrow(() -> new CustomerRefundException("비회원은 환불이 불가합니다."));
	}

}
