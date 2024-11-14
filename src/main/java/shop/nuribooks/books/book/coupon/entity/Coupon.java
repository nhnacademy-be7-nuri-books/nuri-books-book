package shop.nuribooks.books.book.coupon.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.nuribooks.books.book.coupon.enums.ExpirationType;

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
	@Size(min = 3, max = 50)
	private String name;

	@NotNull
	private Long discount;

	@NotNull
	private BigDecimal minimumOrderPrice;

	@NotNull
	private BigDecimal maximumDiscountPrice;

	@NotNull
	private LocalDate createdAt;

	private LocalDate expiredAt;

	private int period;

	@NotNull
	private ExpirationType expirationType;

}
