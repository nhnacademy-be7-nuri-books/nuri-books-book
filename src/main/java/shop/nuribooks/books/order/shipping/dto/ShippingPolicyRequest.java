package shop.nuribooks.books.order.shipping.dto;

import jakarta.validation.constraints.NotNull;

public record ShippingPolicyRequest(
	@NotNull(message = "배송 정책은 필수입니다.")
	long shippingPolicyId
) {
}
