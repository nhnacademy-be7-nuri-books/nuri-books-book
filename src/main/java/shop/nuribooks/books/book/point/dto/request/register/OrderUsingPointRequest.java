package shop.nuribooks.books.book.point.dto.request.register;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import shop.nuribooks.books.book.point.entity.PointPolicy;
import shop.nuribooks.books.book.point.entity.child.OrderUsingPoint;
import shop.nuribooks.books.book.point.exception.PointUsingMuchMoreException;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.order.order.entity.Order;

public class OrderUsingPointRequest extends PointHistoryRequest {
	@NotNull
	Order order;

	@NotNull
	BigDecimal amount;

	public OrderUsingPointRequest(Member member, Order order, BigDecimal amount) {
		super(member);
		this.order = order;
		this.amount = amount;
		if (member.getPoint().compareTo(amount) < 0)
			throw new PointUsingMuchMoreException();
	}

	public OrderUsingPoint toEntity(PointPolicy pointPolicy) {
		return OrderUsingPoint.builder()
			.amount(amount.negate())
			.description("사용된 포인트")
			.member(member)
			.pointPolicy(pointPolicy)
			.order(order)
			.build();
	}
}
