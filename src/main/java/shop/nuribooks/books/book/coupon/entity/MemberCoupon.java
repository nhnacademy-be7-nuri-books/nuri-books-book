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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.nuribooks.books.book.coupon.enums.ExpirationType;
import shop.nuribooks.books.exception.coupon.MemberCouponExpiredException;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "member_coupons")
public class MemberCoupon {

	@Id
	@Column(name = "member_coupon_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "coupon_id")
	private Coupon coupon;

	@NotNull
	@JoinColumn(name = "customer_id")
	private Long memberId;

	@NotNull
	private boolean isUsed = false;

	@NotNull
	private LocalDate createdAt;

	@NotNull
	private LocalDate expiredAt;

	@Builder
	MemberCoupon(Coupon coupon, Long memberId) {
		this.coupon = coupon;
		this.memberId = memberId;
		this.isUsed = false;
		this.createdAt = LocalDate.now();
		setExpiredAt(coupon);
	}

	private void setExpiredAt(Coupon coupon) {
		if (coupon.getExpirationType().equals(ExpirationType.DATE)) {
			this.expiredAt = coupon.getCreatedAt();
		} else {
			this.expiredAt = createdAt.plusDays(coupon.getPeriod());
		}
		if (LocalDate.now().isAfter(expiredAt)) {
			throw new MemberCouponExpiredException("쿠폰 기한이 만료되었습니다.");
		}
	}

	public void setUsed() {
		this.isUsed = true;
	}
}
