package shop.nuribooks.books.payment.payment.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.nuribooks.books.order.order.entity.Order;

/**
 * 결제 정보 entity
 *
 * @author nuri
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Comment("결제 정보")
@Table(name = "payments")
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Comment("결제 아이디")
	private Long id;

	@OneToOne
	@JoinColumn(name = "order_id", nullable = false,
		foreignKey = @ForeignKey(name = "FK_payments_to_orders_1"))
	@Comment("연결된 주문 정보")
	private Order order;

	@Column(nullable = false, unique = true)
	String tossPaymentKey;

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	@Comment("연결된 결제 수단 정보")
	private PaymentMethod paymentMethod;

	@Column(nullable = false)
	@Comment("결제 상태")
	private PaymentState paymentState = PaymentState.READY;

	@Column(precision = 9, nullable = false)
	@Comment("결제 금액")
	private BigDecimal unitPrice;

	@Column(nullable = false)
	LocalDateTime requestedAt;

	LocalDateTime approvedAt;

	/**
	 * 결제 정보 생성자 (Builder)
	 *
	 * @param paymentMethod 결제 수단
	 * @param order 주문 정보
	 * @param paymentState 결제 상태
	 */
	@Builder
	public Payment(Order order, String tossPaymentKey, PaymentMethod paymentMethod, PaymentState paymentState,
		BigDecimal unitPrice, LocalDateTime requestedAt, LocalDateTime approvedAt) {
		this.order = order;
		this.tossPaymentKey = tossPaymentKey;
		this.paymentMethod = paymentMethod;
		this.paymentState = paymentState;
		this.unitPrice = unitPrice;
		this.requestedAt = requestedAt;
		this.approvedAt = approvedAt;
	}
}
