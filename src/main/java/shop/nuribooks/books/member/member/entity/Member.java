package shop.nuribooks.books.member.member.entity;

import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.FetchType.*;
import static java.math.BigDecimal.*;
import static shop.nuribooks.books.member.member.entity.StatusType.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
	 * STANDARD, GOLD, PLATINUM, ROYAL
	 */
	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "grade_id")
	private Grade grade;

	/**
	 * ACTIVE, INACTIVE, WITHDRAWN
	 */
	@Enumerated(STRING)
	private StatusType status;

	/**
	 * MALE, FEMALE
	 */
	@Enumerated(STRING)
	private GenderType gender;

	@NotBlank
	@Size(min = 8, max = 20)
	private String userId;

	private LocalDate birthday;

	private LocalDateTime createdAt;

	@Column(precision = 10, scale = 2)
	@NotNull
	private BigDecimal point;

	@Column(precision = 10, scale = 2)
	@NotNull
	private BigDecimal totalPaymentAmount;

	/**
	 * 마지막 로그인 일시
	 */
	private LocalDateTime latestLoginAt = null;

	/**
	 * 탈퇴 일시
	 */
	private LocalDateTime withdrawnAt = null;

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
	}

	/**
	 * 회원 탈퇴 후 1년이 지나면 userId, status, withdrawnAt을 제외한 나머지 필드 soft delete
	 */
	public void changeToSoftDeleted() {
		this.authority = null;
		this.grade = null;
		this.status = null;
		this.gender = null;
		this.birthday = null;
		this.createdAt = null;
		this.point = ZERO;
		this.totalPaymentAmount = ZERO;
		this.latestLoginAt = null;
	}
}
