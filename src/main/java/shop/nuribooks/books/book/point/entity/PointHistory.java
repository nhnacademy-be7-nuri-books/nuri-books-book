package shop.nuribooks.books.book.point.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import shop.nuribooks.books.member.member.entity.Member;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
@DiscriminatorColumn
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "point_histories")
public class PointHistory {
	@Id
	@Column(name = "point_history_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Min(0)
	private BigDecimal amount;

	@NotBlank
	private String description;

	@NotNull
	@Builder.Default
	private LocalDateTime createdAt = LocalDateTime.now();

	private LocalDateTime deletedAt;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "customer_id", nullable = false)
	private Member member;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "point_policy_id", nullable = false)
	private PointPolicy pointPolicy;

	// public PointHistory(BigDecimal amount, String description, Member member, PointPolicy pointPolicy) {
	// 	this.amount = amount;
	// 	this.description = description;
	// 	this.member = member;
	// 	this.pointPolicy = pointPolicy;
	// 	this.createdAt = LocalDateTime.now();
	// }
}
