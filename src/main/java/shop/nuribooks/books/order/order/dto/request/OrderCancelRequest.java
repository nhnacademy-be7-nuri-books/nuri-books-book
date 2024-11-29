package shop.nuribooks.books.order.order.dto.request;

public record OrderCancelRequest(
	String reason,
	Long customerId) {
}
