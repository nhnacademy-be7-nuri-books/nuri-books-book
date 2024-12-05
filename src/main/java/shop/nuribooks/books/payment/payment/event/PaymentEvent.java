package shop.nuribooks.books.payment.payment.event;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.order.order.entity.Order;

@AllArgsConstructor
public class PaymentEvent {
	private Member member;
	private Order order;
	private BigDecimal amount;

	public Member getMember() {
		return this.member;
	}

	public Order getOrder() {
		return this.order;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}
}
