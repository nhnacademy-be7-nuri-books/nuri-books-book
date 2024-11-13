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
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.nuribooks.books.member.member.entity.Member;

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
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Member member;

	@NotNull
	private boolean isUsed = false;

	@NotNull
	private LocalDate createdAt;

	@NotNull
	private LocalDate expiredAt;
}
