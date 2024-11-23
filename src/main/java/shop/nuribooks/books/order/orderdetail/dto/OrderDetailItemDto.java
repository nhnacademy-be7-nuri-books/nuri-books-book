package shop.nuribooks.books.order.orderdetail.dto;

import java.math.BigDecimal;

import shop.nuribooks.books.order.orderdetail.entity.OrderState;

public record OrderDetailItemDto(
	String title,
	BigDecimal price,
	String thumbnailImageUrl,
	BigDecimal discountedPrice,
	int bookCount,
	OrderState orderState
) {
}
