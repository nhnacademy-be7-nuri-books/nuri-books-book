package shop.nuribooks.books.order.order.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.order.wrapping.entity.WrappingPaper;

/**
 * 주문 Entity
 *
 * @author nuri
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "orders")
@Comment("주문")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Comment("주문 아이디")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false,
		foreignKey = @ForeignKey(name = "FK_orders_to_customers_1"))
	@Comment("주문자 정보")
	private Customer customer;

	@Column(precision = 9, nullable = false)
	@Comment("총 결제 금액")
	private BigDecimal paymentPrice;

	@Column(precision = 9, nullable = false)
	@Comment("순수 금액")
	private BigDecimal booksPrice;

	@Column(nullable = false)
	@Comment("주문 일시")
	private LocalDateTime orderedAt;

	@Column(precision = 9, nullable = false)
	@Comment("포장비")
	private BigDecimal wrappingPrice = BigDecimal.ZERO;

	@Comment("예상 배송 날짜")
	private LocalDate expectedDeliveryAt;

	@Comment("주문명")
	@Setter
	private String title;

	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "wrapping_paper_id", nullable = true,
		foreignKey = @ForeignKey(name = "FK_orders_to_wrapping_papers_1"))
	private WrappingPaper wrappingPaper;

	/**
	 * 주문 생성자 (builder)
	 *
	 * @param customer 주문자 정보
	 * @param paymentPrice 결제 가격
	 * @param orderedAt 주문 일시
	 * @param wrappingPrice 포장 비용
	 * @param expectedDeliveryAt 예상 배송 날짜
	 * @param wrappingPaper 포장지
	 * @param booksPrice 도서 총 가격
	 */
	@Builder
	public Order(Customer customer,
		BigDecimal paymentPrice,
		LocalDateTime orderedAt,
		BigDecimal wrappingPrice,
		LocalDate expectedDeliveryAt,
		WrappingPaper wrappingPaper,
		BigDecimal booksPrice) {
		this.customer = customer;
		this.paymentPrice = paymentPrice;
		this.orderedAt = orderedAt;
		this.wrappingPrice = wrappingPrice != null ? wrappingPrice : BigDecimal.ZERO;
		this.expectedDeliveryAt = expectedDeliveryAt;
		this.wrappingPaper = wrappingPaper;
		this.booksPrice = booksPrice;
	}

}
