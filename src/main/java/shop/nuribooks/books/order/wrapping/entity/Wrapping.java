package shop.nuribooks.books.order.wrapping.entity;

import org.hibernate.annotations.Comment;

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
import lombok.NoArgsConstructor;
import shop.nuribooks.books.order.order.entity.Order;

/**
 * 주문에 대한 포장 정보" entity
 *
 * @author nuri
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "wrappings")
@Comment("주문에 대한 포장 정보")
public class Wrapping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Comment("포장 아이디")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false,
	foreignKey = @ForeignKey(name = "FK_orders_TO_wrappings_1"))
	@Comment("연결된 주문 정보")
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "wrapping_paper_id", nullable = false,
		foreignKey = @ForeignKey(name = "FK_wrapping_papers_TO_wrappings_1"))
	@Comment("연결된 포장지 정보")
	private WrappingPaper wrappingPaper;

	/**
	 * 포장 생성자 (Builder)
	 *
	 * @param order 주문 정보
	 * @param wrappingPaper 포장지 정보
	 */
	public Wrapping(Order order, WrappingPaper wrappingPaper) {
		this.order = order;
		this.wrappingPaper = wrappingPaper;
	}
}
