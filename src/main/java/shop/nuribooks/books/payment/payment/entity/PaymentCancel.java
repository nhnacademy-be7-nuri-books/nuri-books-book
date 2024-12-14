package shop.nuribooks.books.payment.payment.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@Table(name = "payment_cancels")
public class PaymentCancel {

	@Column(nullable = false, unique = true)
	String transactionKey;
	@Column(nullable = false)
	String cancelReason;
	@Column(nullable = false)
	LocalDateTime canceledAt;

	@Id
	@Column(name = "id")
	@MapsId
	private Long id;

	@OneToOne
	@MapsId
	@JoinColumn(name = "id")
	private Payment payment;

}
