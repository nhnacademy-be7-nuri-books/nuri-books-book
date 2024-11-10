package shop.nuribooks.books.book.point.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.nuribooks.books.book.point.dto.request.PointPolicyRequest;
import shop.nuribooks.books.book.point.enums.PolicyType;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "point_policies")
public class PointPolicy {
	@Id
	@Column(name = "point_policy_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@NotNull
	private PolicyType policyType;

	@NotNull
	@Setter
	private String name;

	// 포인트 적용량 값. type이 fixed -> 그냥 적용 값. rated -> 적용 비율
	@NotNull
	@Min(0)
	@Setter
	private BigDecimal amount;

	@NotNull
	private LocalDateTime createdAt;

	private LocalDateTime deletedAt;

	@Builder
	public PointPolicy(
		PolicyType policyType,
		String name,
		BigDecimal amount
	) {
		this.policyType = policyType;
		this.name = name;
		this.amount = amount;
		this.createdAt = LocalDateTime.now();
	}

	public void update(PointPolicyRequest pointPolicyRequest) {
		this.policyType = pointPolicyRequest.policyType();
		this.amount = pointPolicyRequest.amount();
	}

	public void delete() {
		this.deletedAt = LocalDateTime.now();
	}
}
