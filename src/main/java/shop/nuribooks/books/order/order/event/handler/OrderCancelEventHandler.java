package shop.nuribooks.books.order.order.event.handler;

import java.math.BigDecimal;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.point.dto.request.register.OrderCancelReturningPointRequest;
import shop.nuribooks.books.book.point.enums.PolicyName;
import shop.nuribooks.books.book.point.service.PointHistoryService;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.order.event.OrderCancelPointEvent;

@Component
@RequiredArgsConstructor
public class OrderCancelEventHandler {
	private final PointHistoryService pointHistoryService;

	// point event listener 추가.
	@EventListener
	public void createPointHistory(OrderCancelPointEvent event) {
		Member member = event.getMember();
		Order order = event.getOrder();
		BigDecimal amount = event.getAmount();
		OrderCancelReturningPointRequest orderCancelReturningPointRequest = new OrderCancelReturningPointRequest(member,
			order,
			amount);
		pointHistoryService.registerPointHistory(orderCancelReturningPointRequest, PolicyName.CANCEL);
	}
}
