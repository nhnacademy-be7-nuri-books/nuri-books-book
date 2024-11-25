package shop.nuribooks.books.order.order.event.handler;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.point.dto.request.register.OrderUsingPointRequest;
import shop.nuribooks.books.book.point.enums.PolicyName;
import shop.nuribooks.books.book.point.service.PointHistoryService;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.order.event.PointUsedEvent;

@Component
@RequiredArgsConstructor
public class PointUsedEventHandler {
	private final PointHistoryService pointHistoryService;

	// point event listener 추가.
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void createPointHistory(PointUsedEvent event) {
		Member member = event.getMember();
		Order order = event.getOrder();
		BigDecimal amount = event.getAmount();
		OrderUsingPointRequest orderUsingPointRequest = new OrderUsingPointRequest(member, order, amount);
		pointHistoryService.registerPointHistory(orderUsingPointRequest, PolicyName.USING);
	}
}
