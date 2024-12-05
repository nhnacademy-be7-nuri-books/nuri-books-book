package shop.nuribooks.books.payment.payment.event.handler;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.point.dto.request.register.OrderSavingPointRequest;
import shop.nuribooks.books.book.point.enums.PolicyName;
import shop.nuribooks.books.book.point.service.PointHistoryService;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.payment.payment.event.PaymentEvent;

@Component
@RequiredArgsConstructor
public class PaymentEventHandler {
	private final PointHistoryService pointHistoryService;
	private final MemberRepository memberRepository;

	// point event listener 추가.
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void createPointHistory(PaymentEvent event) {
		Member member = event.getMember();
		Order order = event.getOrder();
		BigDecimal amount = event.getAmount();
		OrderSavingPointRequest orderSavingPointRequest = new OrderSavingPointRequest(member, order, amount);
		pointHistoryService.registerPointHistory(orderSavingPointRequest, PolicyName.SAVE);
	}

	// 총 사용 금액 로직 추가.
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void updateTotalPaymentAmount(PaymentEvent event) {
		Member member = event.getMember();
		Order order = event.getOrder();

		BigDecimal prevAmount = member.getTotalPaymentAmount();
		member.setTotalPaymentAmount(prevAmount.add(order.getBooksPrice()));
		memberRepository.save(member);
	}
}
