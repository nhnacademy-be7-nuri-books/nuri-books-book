package shop.nuribooks.books.member.member.event.handler;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.coupon.service.CouponService;
import shop.nuribooks.books.book.point.dto.request.register.PointHistoryRequest;
import shop.nuribooks.books.book.point.enums.PolicyName;
import shop.nuribooks.books.book.point.service.PointHistoryService;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.event.RegisteredEvent;

@Component
@RequiredArgsConstructor
public class RegisterEventHandler {
	private final PointHistoryService pointHistoryService;
	private final CouponService couponService;

	// point event listener 추가.
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void createPointHistory(RegisteredEvent event) {
		Member member = event.getMember();
		PointHistoryRequest pointHistoryRequest = new PointHistoryRequest(member);
		pointHistoryService.registerPointHistory(pointHistoryRequest, PolicyName.WELCOME);
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void createCoupon(RegisteredEvent event) {
		Member member = event.getMember();
		couponService.issueWelcomeCoupon(member);
	}
}
