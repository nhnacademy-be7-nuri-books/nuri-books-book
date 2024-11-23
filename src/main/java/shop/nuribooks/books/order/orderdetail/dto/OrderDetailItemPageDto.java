package shop.nuribooks.books.order.orderdetail.dto;

import java.util.List;

public record OrderDetailItemPageDto(
	List<OrderDetailItemDto> orderDetailItem,
	Long totalCount
) {
}
