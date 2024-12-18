package shop.nuribooks.books.member.member.entity;

import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.FetchType.*;
import static java.math.BigDecimal.*;
import static shop.nuribooks.books.member.member.entity.StatusType.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.nuribooks.books.cart.entity.Cart;
import shop.nuribooks.books.member.address.entity.Address;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.grade.entity.Grade;

/**
 * @author Jprotection
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "members")
public class Member {

	@Id
	private Long id;

	/**
	 * MapsId 애노테이션은 @id로 지정한 컬럼에 @OneToOne 이나 @ManyToOne 관계를 매핑시킨다.
	 */
	@OneToOne(fetch = LAZY)
	@MapsId
	@JoinColumn(name = "customer_id")
	private Customer customer;

	/**
	 * ADMIN, MEMBER, SELLER
	 */
	@Enumerated(STRING)
	private AuthorityType authority;

	/**
	 * STANDARD, ROYAL, GOLD, PLATINUM
	 */
	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "grade_id")
	private Grade grade;

	@OneToMany(mappedBy = "member", fetch = LAZY, cascade = CascadeType.PERSIST)
	@Builder.Default
	private List<Address> addressList = new ArrayList<>();

	/**
	 * ACTIVE, INACTIVE, WITHDRAWN
	 */
	@Enumerated(STRING)
	private StatusType status;

	/**
	 * MALE, FEMALE, OTHER
	 */
	@Enumerated(STRING)
	private GenderType gender;

	@NotBlank
	@Column(unique = true, length = 100)
	private String username;

	private LocalDate birthday;

	private LocalDateTime createdAt;

	@Column(precision = 10)
	@NotNull
	@Setter
	private BigDecimal point;

	@Column(precision = 10)
	@NotNull
	@Setter
	private BigDecimal totalPaymentAmount;

	/**
	 * 마지막 로그인 일시
	 */
	private LocalDateTime latestLoginAt;

	/**
	 * 탈퇴 일시
	 */
	private LocalDateTime withdrawnAt;

	@OneToOne(mappedBy = "member")
	private Cart cart;

	/**
	 * 마지막 로그일 날짜로부터 90일이 지나면 상태를 INACTIVE로 변경
	 */
	public void changeToInactive() {
		this.status = INACTIVE;
	}

	/**
	 * 회원 탈퇴 시 상태를 탈퇴됨으로, 탈퇴 일시를 현재 시간으로 초기화
	 */
	public void changeToWithdrawn() {
		this.status = WITHDRAWN;
		this.withdrawnAt = LocalDateTime.now();
		this.point = ZERO;
	}

	/**
	 * 최근 로그인 시간을 업데이트
	 */
	public void updateLatestLoginAt() {
		this.latestLoginAt = LocalDateTime.now();
	}

	/**
	 * 휴면 상태를 해지하면 status를 ACTIVE로 업데이트
	 */
	public void reactiveMemberStatus() {
		this.status = ACTIVE;
	}

	public void addAddress(Address address) {
		address.setMember(this);
		addressList.add(address);
	}
}
