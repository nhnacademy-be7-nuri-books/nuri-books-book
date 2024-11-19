package shop.nuribooks.books.order.shipping.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import shop.nuribooks.books.order.shipping.entity.ShippingPolicy;

public record ShippingPolicyRequest(
	@NotBlank(message = "배송비 설정은 필수입니다.")
	Integer shippingFee,

	LocalDateTime expiration,

	@NotBlank(message = "배송비 책정을 위한 최소주문금액은 필수입니다.")
	@Min(value = 0, message = "최소주문금액은 0원 이상이어야합니다.")
	BigDecimal minimumOrderPrice
) {
	public ShippingPolicy toEntity() {
		return ShippingPolicy.builder()
			.shippingFee(shippingFee)
			.expiration(expiration)
			.minimumOrderPrice(minimumOrderPrice)
			.build();
	}
}
