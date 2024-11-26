package shop.nuribooks.books.order.refund.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
import shop.nuribooks.books.order.orderdetail.dto.OrderDetailItemDto;
import shop.nuribooks.books.order.orderdetail.dto.OrderDetailItemPageDto;
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
import shop.nuribooks.books.payment.payment.dto.PaymentInfoDto;

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
		BigDecimal deductedAmount = BigDecimal.valueOf(2500L);

		BigDecimal totalRefundAmount =
			paymentPrice.subtract(deductedAmount).compareTo(BigDecimal.ZERO) > 0 ?
				paymentPrice.subtract(deductedAmount) : BigDecimal.ZERO;

		pageable = PageRequest.of(0, 5);
		OrderDetailItemPageDto orderDetailItem = orderDetailRepository.findOrderDetail(orderId, pageable);

		Page<OrderDetailItemDto> orderListResponses =
			new PageImpl(orderDetailItem.orderDetailItem(), pageable, orderDetailItem.totalCount());

		// TODO : 결제 정보는 주문이 다 되면 추가한다.
		PaymentInfoDto paymentInfoDto = null;
		// PaymentInfoDto paymentInfo = paymentRepository.findPaymentInfo(orderId);

		RefundInfo refundInfo = new RefundInfo(paymentPrice, deductedAmount, totalRefundAmount);

		return new RefundInfoResponse(orderListResponses, paymentInfoDto, refundInfo);
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

		RefundReturningPointRequest refundReturningPointRequest = new RefundReturningPointRequest(member,
			refund, refund.getRefundAmount());
		//환불 포인트에 저장
		pointHistoryService.registerPointHistory(refundReturningPointRequest, PolicyName.REFUND);
		// 회원 포인트로 변환 (현재는 구매한 금액에 대해서만 포인트로 반환한다.)

		// TODO: 주문으로 인해 적립된 포인트 기록을 삭제 및 누적 구매금액도 다시 감소시켜야 한다.

		Refund saved = refundRepository.save(refund);
		return RefundResponse.of(saved);
	}

	private Member getMember(Order order) {
		Long customerId = order.getCustomer().getId();
		return memberRepository.findById(customerId)
			.orElseThrow(() -> new CustomerRefundException("비회원은 환불이 불가합니다."));
	}

}
