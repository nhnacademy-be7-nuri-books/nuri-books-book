package shop.nuribooks.books.order.refund.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.point.dto.request.register.RefundReturningPointRequest;
import shop.nuribooks.books.book.point.enums.PolicyName;
import shop.nuribooks.books.book.point.service.PointHistoryService;
import shop.nuribooks.books.exception.order.CustomerRefundException;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;
import shop.nuribooks.books.order.orderDetail.entity.OrderDetail;
import shop.nuribooks.books.order.orderDetail.service.OrderDetailService;
import shop.nuribooks.books.order.refund.dto.request.RefundRequest;
import shop.nuribooks.books.order.refund.dto.response.RefundResponse;
import shop.nuribooks.books.order.refund.entity.Refund;
import shop.nuribooks.books.order.refund.repository.RefundRepository;

@RequiredArgsConstructor
@Service
public class RefundServiceImpl implements RefundService {

	private final RefundRepository refundRepository;
	private final OrderDetailService orderDetailService;
	private final MemberRepository memberRepository;
	private final PointHistoryService pointHistoryService;

	@Override
	@Transactional
	public RefundResponse refund(RefundRequest refundRequest) {

		// 주문상세 가져오기
		OrderDetail orderDetail = orderDetailService.getOrderDetail(refundRequest.orderDetailId());

		// 멤버 가져오기
		Member member = getMember(orderDetail);

		orderDetailService.refundUpdateStateAndStock(orderDetail);
		// 회원 포인트로 변환 (현재는 구매한 금액에 대해서만 포인트로 반환한다.)
		// TODO: 이후 쿠폰 적용 여부에 따라 금액 계산 적용
		BigDecimal refundAmount = orderDetail.getUnitPrice().multiply(BigDecimal.valueOf(orderDetail.getCount()));

		// TODO: 주문으로 인해 적립된 포인트 기록을 삭제해주어야 한다.

		// 누적 구매금액도 다시 감소시켜야 한다.

		// 쿠폰 반환

		Refund refund = refundRequest.toEntity(orderDetail, refundAmount);

		RefundReturningPointRequest refundReturningPointRequest = new RefundReturningPointRequest(member, refund,
			refundAmount);
		//환불 포인트에 저장
		pointHistoryService.registerPointHistory(refundReturningPointRequest, PolicyName.REFUND);

		Refund saved = refundRepository.save(refund);
		return RefundResponse.of(saved);
	}

	private Member getMember(OrderDetail orderDetail) {
		Long customerId = orderDetail.getOrder().getCustomer().getId();
		return memberRepository.findById(customerId)
			.orElseThrow(() -> new CustomerRefundException("비회원은 환불이 불가합니다."));
	}

}
