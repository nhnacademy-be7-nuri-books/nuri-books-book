package shop.nuribooks.books.book.coupon.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.nuribooks.books.book.coupon.dto.CouponRequest;
import shop.nuribooks.books.book.coupon.enums.ExpirationType;
import shop.nuribooks.books.book.point.enums.PolicyType;

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
	@Size(min = 2, max = 50)
	private String name;

	@NotNull
	private PolicyType policyType;

	@NotNull
	private int discount;

	@NotNull
	private BigDecimal minimumOrderPrice;

	@NotNull
	private BigDecimal maximumDiscountPrice;

	@NotNull
	private LocalDate createdAt;

	private LocalDate expiredAt;

	private Integer period;

	@NotNull
	private ExpirationType expirationType;

	private LocalDateTime expireDate;

	@Builder
	public Coupon(String name, PolicyType policyType, int discount, BigDecimal minimumOrderPrice,
		BigDecimal maximumDiscountPrice, LocalDate createdAt, LocalDate expiredAt,
		int period, ExpirationType expirationType, LocalDateTime expireDate) {
		this.name = name;
		this.policyType = policyType;
		this.discount = discount;
		this.minimumOrderPrice = minimumOrderPrice;
		this.maximumDiscountPrice = maximumDiscountPrice;
		this.createdAt = createdAt;
		this.expiredAt = expiredAt;
		this.period = period;
		this.expirationType = expirationType;
		this.expireDate = expireDate;
	}

	public void update(CouponRequest request) {
		this.name = request.name();
		this.policyType = request.toEntity().getPolicyType();
		this.discount = request.discount();
		this.minimumOrderPrice = request.minimumOrderPrice();
		this.maximumDiscountPrice = request.maximumDiscountPrice();
		this.createdAt = request.createdAt();
		this.expiredAt = request.expiredAt();
		this.period = request.period();
		this.expirationType = request.expirationType();
	}

	public void expire() {
		this.expireDate = LocalDateTime.now();
	}
}
