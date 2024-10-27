package shop.nuribooks.books.member.member.entity;

import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.FetchType.*;
import static shop.nuribooks.books.member.member.entity.StatusEnum.*;

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
	@NotNull
	@Enumerated(STRING)
	private AuthorityEnum authority;

	/**
	 * STANDARD, GOLD, PLATINUM, ROYAL
	 */
	@NotNull
	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "grade_id")
	private Grade grade;

	/**
	 * ACTIVE, INACTIVE, WITHDRAWN
	 */
	@NotNull
	@Enumerated(STRING)
	private StatusEnum status;

	@NotBlank
	@Size(min = 8, max = 20)
	private String userId;

	@NotNull
	private LocalDate birthday;

	@NotNull
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
	private LocalDateTime latestLoginAt;

	/**
	 * 탈퇴 일시
	 */
	private LocalDateTime withdrawnAt;

	public void changeToWithdrawn() {
		this.status = INACTIVE;
		this.withdrawnAt = LocalDateTime.now();
	}
}
