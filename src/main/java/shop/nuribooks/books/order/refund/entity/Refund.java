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
import shop.nuribooks.books.order.orderDetail.entity.OrderDetail;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "refunds")
public class Refund {

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_detail_id")
	OrderDetail orderDetail;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "refund_id")
	private Long id;
	private String reason;

	private BigDecimal refundAmount;

	@Builder
	private Refund(OrderDetail orderDetail, String reason, BigDecimal refundAmount) {
		this.orderDetail = orderDetail;
		this.reason = reason;
		this.refundAmount = refundAmount;
	}
}
