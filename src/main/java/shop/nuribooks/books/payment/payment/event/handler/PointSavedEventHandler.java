package shop.nuribooks.books.payment.payment.event.handler;

import java.math.BigDecimal;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.point.dto.request.register.OrderSavingPointRequest;
import shop.nuribooks.books.book.point.enums.PolicyName;
import shop.nuribooks.books.book.point.service.PointHistoryService;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.payment.payment.event.PointSavedEvent;

@Component
@RequiredArgsConstructor
public class PointSavedEventHandler {
	private final PointHistoryService pointHistoryService;

	// point event listener 추가.
	@EventListener
	public void createPointHistory(PointSavedEvent event) {
		Member member = event.getMember();
		Order order = event.getOrder();
		BigDecimal amount = event.getAmount();
		OrderSavingPointRequest orderSavingPointRequest = new OrderSavingPointRequest(member, order, amount);
		pointHistoryService.registerPointHistory(orderSavingPointRequest, PolicyName.SAVE);
	}
}
