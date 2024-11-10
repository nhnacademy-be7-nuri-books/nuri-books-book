package shop.nuribooks.books.payment.payment.entity;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
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

	@ManyToOne
	@JoinColumn(name = "payment_method_id", nullable = false,
	foreignKey =  @ForeignKey(name = "FK_payments_to_payment_methods_1)"))
	@Comment("연결된 결제 수단 정보")
	private PaymentMethod paymentMethod;

	@OneToOne
	@JoinColumn(name = "order_id", nullable = false,
		foreignKey = @ForeignKey(name = "FK_payments_to_orders_1"))
	@Comment("연결된 주문 정보")
	private Order order;


	@Column(nullable = false)
	@Comment("결제 상태")
	private PaymentState paymentState = PaymentState.PENDING;

	/**
	 * 결제 정보 생성자 (Builder)
	 *
	 * @param paymentMethod 결제 수단
	 * @param order 주문 정보
	 * @param paymentState 결제 상태
	 */
	public Payment(PaymentMethod paymentMethod, Order order, PaymentState paymentState) {
		this.paymentMethod = paymentMethod;
		this.order = order;
		this.paymentState = paymentState;
	}
}
