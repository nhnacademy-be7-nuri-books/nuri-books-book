package shop.nuribooks.books.order.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import shop.nuribooks.books.order.order.entity.Order;

public record OrderTempRegisterResponse(
	Long orderId, // 주문 아이디
	String orderName, // 주문 명
	BigDecimal paymentPrice, // 결제 금액
	BigDecimal wrappingPrice, // 포장 비용
	LocalDateTime orderedAt // 주문 시간
) {
	public static OrderTempRegisterResponse of(Order order, String orderName){
		return new OrderTempRegisterResponse(
			order.getId(),
			orderName,
			order.getPaymentPrice(),
			order.getWrappingPrice(),
			order.getOrderedAt()
		);
	}
}
