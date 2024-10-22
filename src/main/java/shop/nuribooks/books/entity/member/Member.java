package shop.nuribooks.books.entity.member;

import static jakarta.persistence.FetchType.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

	@Id
	private Long id;

	@OneToOne(fetch = LAZY)
	@MapsId // @MapsId는 @id로 지정한 컬럼에 @OneToOne 이나 @ManyToOne 관계를 매핑시키는 역할
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@NotNull
	private AuthorityEnum authority;

	@NotNull
	private GradeEnum grade;

	@NotNull
	private StatusEnum status;

	@NotBlank
	@Size(max = 20)
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

	private LocalDateTime latestLoginAt;

	private LocalDateTime resignedAt;

	public Member(Customer customer, AuthorityEnum authority, GradeEnum grade, StatusEnum status, String userId,
		LocalDate birthday, LocalDateTime createdAt, BigDecimal point, BigDecimal totalPaymentAmount) {
		this.customer = customer;
		this.authority = authority;
		this.grade = grade;
		this.status = status;
		this.userId = userId;
		this.birthday = birthday;
		this.createdAt = createdAt;
		this.point = point;
		this.totalPaymentAmount = totalPaymentAmount;
	}

	public void changeStatus(StatusEnum status) {
		this.status = status;
	}
}
