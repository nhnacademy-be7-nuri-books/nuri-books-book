package shop.nuribooks.books.order.shipping.entity;

import java.time.LocalDateTime;

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
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.shipping.dto.ShippingResponse;
import shop.nuribooks.books.order.shipping.util.InvoiceNumberGenerator;

/**
 * 배송지 entity
 *
 * @author nuri
 */
@Entity
@Getter
@Table(name = "shippings")
@Comment("배송 정보")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shipping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Comment("배송 아이디")
	private Long id;

	@OneToOne
	@JoinColumn(name = "order_id", nullable = false,
		foreignKey = @ForeignKey(name = "FK_shippings_to_orders_1"))
	@Comment("연관된 주문 정보")
	private Order order;

	@ManyToOne
	@JoinColumn(name = "shipping_policy_id", nullable = false,
		foreignKey = @ForeignKey(name = "FK_shippings_to_shipping_policies_1"))
	@Comment("배송 정책 정보")
	private ShippingPolicy shippingPolicy;

	@Column(length = 50, nullable = false)
	@Comment("받는 사람 이름")
	@NotBlank
	private String recipientName;

	@Column(length = 100, nullable = false)
	@Comment("받는 사람 주소")
	@NotBlank
	private String recipientAddress;

	@Column(length = 50, nullable = false)
	@Comment("받는 사람 상세 주소")
	private String recipientAddressDetail;

	@Column(length = 50, nullable = false)
	@Comment("받는 사람 우편 번호")
	@NotBlank
	private String recipientZipcode;

	@Column(length = 11, nullable = false)
	@Comment("받는 사람 연락처")
	@NotBlank
	private String recipientPhoneNumber;

	@Column(length = 50, nullable = false)
	@Comment("보내는 사람 이름")
	@NotBlank
	private String senderName;

	@Column(length = 11, nullable = false)
	@Comment("보내는 사람 전화번호")
	@NotBlank
	private String senderPhoneNumber;

	@Comment("출고일")
	private LocalDateTime shippingAt;

	@Column(length = 15)
	@Comment("주문 송장 번호")
	private String orderInvoiceNumber;

	@Comment("배송 완료 일시")
	private LocalDateTime shippingCompletedAt;

	public ShippingResponse toResponseDto() {
		return ShippingResponse.builder()
			.id(id)
			.orderId(order.getId())
			.orderInvoiceNumber(orderInvoiceNumber)
			.recipientName(recipientName)
			.recipientPhoneNumber(recipientPhoneNumber)
			.recipientAddress(recipientAddress)
			.recipientAddressDetail(recipientAddressDetail)
			.recipientZipcode(recipientZipcode)
			.senderName(senderName)
			.senderPhoneNumber(senderPhoneNumber)
			.shippingPolicy(shippingPolicy)
			.shippingCompletedAt(shippingCompletedAt)
			.shippingAt(shippingAt)
			.build();
	}

	public void startDelivery() {
		shippingAt = LocalDateTime.now();
		orderInvoiceNumber = InvoiceNumberGenerator.generateInvoiceNumber();
	}
}
