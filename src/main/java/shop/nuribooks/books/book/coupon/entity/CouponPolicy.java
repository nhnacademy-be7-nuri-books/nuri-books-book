package shop.nuribooks.books.book.coupon.entity;

import static jakarta.persistence.EnumType.*;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.nuribooks.books.book.coupon.enums.DiscountType;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "coupon_policies")
public class CouponPolicy {
	@Id
	@Column(name = "coupon_policy_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@
		Size(min = 2, max = 50)
	private String name;

	@NotNull
	@Enumerated(STRING)
	private DiscountType discountType;

	@NotNull
	private BigDecimal minimumOrderPrice;

	private BigDecimal maximumDiscountPrice;

	@NotNull
	private int discount;

}
