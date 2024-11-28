package shop.nuribooks.books.book.point.dto.request.register;

import java.math.BigDecimal;
import java.math.RoundingMode;

import jakarta.validation.constraints.NotNull;
import shop.nuribooks.books.book.point.entity.PointPolicy;
import shop.nuribooks.books.book.point.entity.child.OrderSavingPoint;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.order.order.entity.Order;

public class OrderSavingPointRequest extends PointHistoryRequest {
	@NotNull
	Order order;

	@NotNull
	BigDecimal orderPrice;

	public OrderSavingPointRequest(Member member, Order order, BigDecimal orderPrice) {
		super(member);
		this.order = order;
		this.orderPrice = orderPrice;
	}

	@Override
	public OrderSavingPoint toEntity(PointPolicy pointPolicy) {
		Grade grade = member.getGrade();
		return OrderSavingPoint.builder()
			// 계산 로직 수정하기. order price가 순수금액으로 바뀌어야할듯.
			// 일단은 주문 상세에서 도서 가격 합산해서 포인트 사용값 뺀게 순수금액인듯?
			.amount(this.orderPrice
				// 기본 적립률 + 등급 적립률 합쳐서 곱하기.
				.multiply(pointPolicy.getAmount().add(BigDecimal.valueOf(grade.getPointRate())))
				// 100으로 나누기
				.divide(BigDecimal.valueOf(100))
				// 소수점 절삭.
				.setScale(0, RoundingMode.UP))
			// book name 추가하기.
			.description(grade.getName() + " 등급 도서 구매 포인트 적립(기본 적립율 포함)")
			.member(member)
			.pointPolicy(pointPolicy)
			.order(order)
			.build();
	}
}
