package shop.nuribooks.books.book.coupon.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import shop.nuribooks.books.order.order.entity.Order;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
@Table(name = "all_applied_coupons")
public class AllAppliedCoupon {

	@Id
	@Column(name = "id")
	@MapsId
	private Long id;

	@OneToOne
	@MapsId
	@JoinColumn(name = "id")
	private MemberCoupon memberCoupon;

	@OneToOne
	@JoinColumn(name = "order_id")
	private Order order;

	@PositiveOrZero
	@Column(precision = 9, nullable = false)
	private BigDecimal discountPrice;
}
