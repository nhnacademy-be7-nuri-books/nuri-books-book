package shop.nuribooks.books.book.coupon.entity;

import static jakarta.persistence.EnumType.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
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
import shop.nuribooks.books.book.coupon.enums.CouponType;
import shop.nuribooks.books.book.coupon.enums.ExpirationType;
import shop.nuribooks.books.book.coupon.enums.IssuanceType;
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

	@Enumerated(STRING)
	private CouponType couponType;

	@NotNull
	private PolicyType policyType;

	@NotNull
	private int discount;

	@NotNull
	private BigDecimal minimumOrderPrice;

	@NotNull
	private BigDecimal maximumDiscountPrice;

	@NotNull
	private ExpirationType expirationType;

	private Integer period;

	private LocalDate expiredAt;

	@Enumerated(STRING)
	private IssuanceType issuanceType;

	private int quantity;

	@NotNull
	private LocalDate createdAt;

	private LocalDateTime deletedAt;

	// @Builder
	// public Coupon(String name, PolicyType policyType, int discount, BigDecimal minimumOrderPrice,
	// 	BigDecimal maximumDiscountPrice, LocalDate createdAt, LocalDate expiredAt,
	// 	int period, ExpirationType expirationType, LocalDateTime expiredDate, CouponType couponType) {
	// 	this.name = name;
	// 	this.policyType = policyType;
	// 	this.discount = discount;
	// 	this.minimumOrderPrice = minimumOrderPrice;
	// 	this.maximumDiscountPrice = maximumDiscountPrice;
	// 	this.createdAt = createdAt;
	// 	this.expiredAt = expiredAt;
	// 	this.period = period;
	// 	this.expirationType = expirationType;
	// 	this.deletedAt = expiredDate;
	// 	this.couponType = couponType;
	// }

	@Builder
	public Coupon(String name, CouponType couponType, PolicyType policyType, int discount, BigDecimal minimumOrderPrice,
		BigDecimal maximumDiscountPrice, ExpirationType expirationType, Integer period, LocalDate expiredAt,
		IssuanceType issuanceType, int quantity) {
		this.name = name;
		this.couponType = couponType;
		this.policyType = policyType;
		this.discount = discount;
		this.minimumOrderPrice = minimumOrderPrice;
		this.maximumDiscountPrice = maximumDiscountPrice;
		this.expirationType = expirationType;
		this.period = period;
		this.expiredAt = expiredAt;
		this.issuanceType = issuanceType;
		this.quantity = quantity;
		this.createdAt = LocalDate.now();
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
		this.couponType = request.couponType();
	}

	public void expire() {
		this.deletedAt = LocalDateTime.now();
	}
}
