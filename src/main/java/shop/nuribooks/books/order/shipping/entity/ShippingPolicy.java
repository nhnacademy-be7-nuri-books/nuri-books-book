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

/**
 * 배송비 정책 entity
 *
 * @author nuri
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "shipping_policies")
@Getter
@Comment("배송비 정책")
public class ShippingPolicy {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Comment("배송비 정책 ID")
	private Long id;
	
	@Comment("배송비")
	private int shippingFee;

	@Column(nullable = true)
	@Comment("적용 날짜")
	private LocalDateTime appliedDatetime;

	@Column(precision = 9, nullable = false)
	@Comment("최소 주문 금액")
	private BigDecimal minimumOrderPrice;

	/**
	 * 배송비 정책 생성자 (Builder)
	 *
	 * @param shippingFee 배송비
	 * @param appliedDatetime 적용 날짜
	 * @param minimumOrderPrice 최소 주문 금액
	 */
	@Builder
	public ShippingPolicy(int shippingFee, LocalDateTime appliedDatetime, BigDecimal minimumOrderPrice) {
		this.shippingFee = shippingFee;
		this.appliedDatetime = appliedDatetime;
		this.minimumOrderPrice = minimumOrderPrice;
	}
}
