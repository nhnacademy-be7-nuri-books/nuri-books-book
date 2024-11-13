package shop.nuribooks.books.book.coupon.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.nuribooks.books.book.coupon.enums.CouponType;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "coupons")
public class Coupon {

	@Id
	@Column(name = "coupon_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private CouponType couponType;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "coupon_policy_id")
	private CouponPolicy couponPolicy;

	@NotNull
	@Size(min = 3, max = 50)
	private String name;

	@NotNull
	private LocalDate createdAt;

	@NotNull
	private LocalDate expiredAt;

	private int period = 0;

}
