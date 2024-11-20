package shop.nuribooks.books.book.point.entity.child;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import shop.nuribooks.books.book.point.entity.PointHistory;
import shop.nuribooks.books.order.order.entity.Order;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
@DiscriminatorValue("order_using_point")
@Table(name = "order_using_point")
public class OrderUsingPoint extends PointHistory {
	// TODO:: 주문 id 추가
	@OneToOne
	@JoinColumn(name = "order_id")
	private Order order;

}
