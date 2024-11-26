package shop.nuribooks.books.order.refund.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.point.dto.request.register.RefundReturningPointRequest;
import shop.nuribooks.books.book.point.enums.PolicyName;
import shop.nuribooks.books.book.point.service.PointHistoryService;
import shop.nuribooks.books.exception.order.CustomerRefundException;
import shop.nuribooks.books.exception.order.OrderNotFoundException;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.order.repository.OrderRepository;
import shop.nuribooks.books.order.orderdetail.entity.OrderDetail;
import shop.nuribooks.books.order.orderdetail.entity.OrderState;
import shop.nuribooks.books.order.orderdetail.repository.OrderDetailRepository;
import shop.nuribooks.books.order.orderdetail.service.OrderDetailService;
import shop.nuribooks.books.order.refund.dto.request.RefundRequest;
import shop.nuribooks.books.order.refund.dto.response.RefundInfo;
import shop.nuribooks.books.order.refund.dto.response.RefundInfoResponse;
import shop.nuribooks.books.order.refund.dto.response.RefundResponse;
import shop.nuribooks.books.order.refund.entity.Refund;
import shop.nuribooks.books.order.refund.repository.RefundRepository;

@RequiredArgsConstructor
@Service
public class RefundServiceImpl implements RefundService {

	private final RefundRepository refundRepository;
	private final OrderDetailService orderDetailService;
	private final OrderDetailRepository orderDetailRepository;
	private final OrderRepository orderRepository;
	private final MemberRepository memberRepository;
	private final PointHistoryService pointHistoryService;

	// 단순 변심에 의한 반품
	@Override
	public RefundInfoResponse getRefundInfoResponse(Long orderId, Pageable pageable) {
		Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("주문을 찾을 수 없습니다."));

		BigDecimal paymentPrice = order.getPaymentPrice();

		// 일단은 반품 배송비 2500원으로 생각
		BigDecimal shippingPrice = BigDecimal.valueOf(2500L);
		BigDecimal savingPointAmount = orderRepository.findOrderSavingPoint(orderId);

		BigDecimal deductedAmount = shippingPrice.add(savingPointAmount);

		BigDecimal totalRefundAmount =
			paymentPrice.subtract(deductedAmount).compareTo(BigDecimal.ZERO) > 0 ?
				paymentPrice.subtract(deductedAmount) : BigDecimal.ZERO;

		RefundInfo refundInfo = new RefundInfo(paymentPrice, shippingPrice, savingPointAmount, deductedAmount,
			totalRefundAmount);

		return new RefundInfoResponse(refundInfo);
	}

	// 단순 변심에 의한 반품
	@Override
	@Transactional
	public RefundResponse refund(Long orderId, RefundRequest refundRequest) {

		// 주문상세 가져오기
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new OrderNotFoundException("주문을 찾을 수 없습니다."));
		List<OrderDetail> orderDetailList = orderDetailRepository.findAllByOrderId(orderId);

		// 상태 변경
		orderDetailList.forEach(
			orderDetail -> orderDetail.setOrderState(OrderState.RETURNED)
		);

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

	private Member getMember(Order order) {
		Long customerId = order.getCustomer().getId();
		return memberRepository.findById(customerId)
			.orElseThrow(() -> new CustomerRefundException("비회원은 환불이 불가합니다."));
	}

}
