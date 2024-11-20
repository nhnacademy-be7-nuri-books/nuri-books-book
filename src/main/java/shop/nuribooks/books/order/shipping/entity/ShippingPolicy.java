package shop.nuribooks.books.order.shipping.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.nuribooks.books.order.shipping.dto.ShippingPolicyRequest;
import shop.nuribooks.books.order.shipping.dto.ShippingPolicyResponse;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Comment("배송비 정책")
@Table(name = "shipping_policies")
@Entity
public class ShippingPolicy {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Comment("배송비 정책 ID")
	private Long id;

	@Comment("배송비")
	private int shippingFee;

	@Column(nullable = true)
	@Comment("만료기한")
	private LocalDateTime expiration;

	@Column(precision = 9, nullable = false)
	@Comment("최소주문금액")
	private BigDecimal minimumOrderPrice;

	/**
	 * 배송비 정책 생성자 (Builder)
	 *
	 * @param shippingFee 배송비
	 * @param expiration 만료기한
	 * @param minimumOrderPrice 최소주문금액
	 */
	@Builder
	public ShippingPolicy(int shippingFee, LocalDateTime expiration, BigDecimal minimumOrderPrice) {
		this.shippingFee = shippingFee;
		this.expiration = expiration;
		this.minimumOrderPrice = minimumOrderPrice;
	}

	public ShippingPolicyResponse toResponseDto() {
		return ShippingPolicyResponse.builder()
			.id(id)
			.expiration(expiration)
			.minimumOrderPrice(minimumOrderPrice)
			.shippingFee(shippingFee)
			.build();
	}

	public void update(ShippingPolicyRequest request) {
		this.shippingFee = request.shippingFee();
		this.expiration = request.expiration();
		this.minimumOrderPrice = request.minimumOrderPrice();
	}

	public void expire() {
		this.expiration = LocalDateTime.now();
	}
}
