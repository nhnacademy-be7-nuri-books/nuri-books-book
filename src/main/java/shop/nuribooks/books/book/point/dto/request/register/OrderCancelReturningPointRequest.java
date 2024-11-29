package shop.nuribooks.books.book.point.dto.request.register;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import shop.nuribooks.books.book.point.entity.PointPolicy;
import shop.nuribooks.books.book.point.entity.child.OrderCancelReturningPoint;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.order.order.entity.Order;

public class OrderCancelReturningPointRequest extends PointHistoryRequest {
	@NotNull
	Order order;
	BigDecimal amount;

	public OrderCancelReturningPointRequest(Member member, Order order, BigDecimal amount) {
		super(member);
		this.order = order;
		this.amount = amount;
	}

	public OrderCancelReturningPoint toEntity(PointPolicy pointPolicy) {
		return OrderCancelReturningPoint.builder()
			.amount(amount)
			.description("주문 취소 시 주문에서 사용한 포인트 (결제를 완료했다면 적립금도 포함")
			.member(member)
			.pointPolicy(pointPolicy)
			.order(order)
			.build();
	}
}
