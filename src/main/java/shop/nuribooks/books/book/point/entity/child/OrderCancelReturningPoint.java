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
@DiscriminatorValue("order_cancel_returning_points")
@Table(name = "order_cancel_returning_points")
public class OrderCancelReturningPoint extends PointHistory {
	@OneToOne
	@JoinColumn(name = "order_id")
	private Order order;

}
