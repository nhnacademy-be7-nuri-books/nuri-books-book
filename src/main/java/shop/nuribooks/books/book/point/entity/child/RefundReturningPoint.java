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
import shop.nuribooks.books.order.refund.entity.Refund;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
@DiscriminatorValue("refund_returning_point")
@Table(name = "refund_returning_point")
public class RefundReturningPoint extends PointHistory {
	// TODO:: 환불 id 추가
	@OneToOne
	@JoinColumn(name = "refund_id")
	private Refund refund;

}
