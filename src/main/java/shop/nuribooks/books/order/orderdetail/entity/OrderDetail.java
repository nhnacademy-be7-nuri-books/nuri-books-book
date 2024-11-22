package shop.nuribooks.books.order.orderdetail.entity;

import java.math.BigDecimal;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.review.entity.Review;
import shop.nuribooks.books.order.order.entity.Order;

/**
 * 주문 상세 Entity
 *
 * @author nuri
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "order_details")
@Comment("주문 상세 정보")
public class OrderDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Comment("주문 상세 아이디")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false,
		foreignKey = @ForeignKey(name = "FK_order_details_to_orders_1"))
	@Comment("연결된 주문 정보")
	private Order order;

	@Column(nullable = false)
	@Comment("주문 상태")
	@Setter
	private OrderState orderState = OrderState.PENDING;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id", nullable = false,
		foreignKey = @ForeignKey(name = "FK_order_details_to_books_1"))
	@Comment("연결된 책 정보")
	private Book book;

	@Column(nullable = false)
	@Comment("주문 수량")
	private int count = 1;

	@Column(precision = 9, nullable = false)
	@Comment("단가")
	private BigDecimal unitPrice;

	@Column(nullable = false)
	@Comment("포장 여부")
	private Boolean isWrapped = false;

	@OneToOne(mappedBy = "orderDetail")
	private Review review;

	/**
	 * 주문 상세 생성자 (Builder)
	 *
	 * @param order 주문 정보
	 * @param orderState 주문 상태
	 * @param book 상품(책) 정보
	 * @param count 상품 수량
	 * @param unitPrice 개별 가격
	 * @param isWrapped 포장 여부
	 */
	@Builder
	public OrderDetail(Order order,
		OrderState orderState,
		Book book,
		int count,
		BigDecimal unitPrice,
		Boolean isWrapped) {
		this.order = order;
		this.orderState = orderState;
		this.book = book;
		this.count = count;
		this.unitPrice = unitPrice;
		this.isWrapped = isWrapped;
	}
}
