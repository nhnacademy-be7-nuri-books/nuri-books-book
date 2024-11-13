package shop.nuribooks.books.book.coupon.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.nuribooks.books.book.point.enums.PolicyType;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "coupon_policies")
public class CouponPolicy {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "coupon_policy_id")
	private Long id;

	@NotNull
	private PolicyType policyType;

	@NotNull
	@Size(min = 2, max = 20)
	private String name;

	@NotNull
	private int discount;

	@NotNull
	@Digits(integer = 10, fraction = 0)
	private BigDecimal minimumOrderPrice;

	@NotNull
	@Digits(integer = 10, fraction = 0)
	private BigDecimal maximumDiscountPrice;

	@NotNull
	private LocalDateTime startedAt;

	private LocalDateTime endedAt;

	@Builder
	public CouponPolicy(PolicyType policyType, String name,
		int discount, BigDecimal minimumOrderPrice, BigDecimal maximumDiscountPrice,
		LocalDateTime startedAt, LocalDateTime endedAt) {
		this.policyType = policyType;
		this.name = name;
		this.discount = discount;
		this.minimumOrderPrice = minimumOrderPrice;
		this.maximumDiscountPrice = maximumDiscountPrice;
		this.startedAt = startedAt;
		this.endedAt = endedAt;
	}
}
