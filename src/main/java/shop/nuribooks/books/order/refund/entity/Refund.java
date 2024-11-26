package shop.nuribooks.books.order.refund.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.nuribooks.books.order.order.entity.Order;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "refunds")
public class Refund {

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	Order order;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "refund_id")
	private Long id;

	private String reason;

	private BigDecimal refundAmount;

	@Builder
	private Refund(Order order, String reason, BigDecimal refundAmount) {
		this.order = order;
		this.reason = reason;
		this.refundAmount = refundAmount;
	}
}
