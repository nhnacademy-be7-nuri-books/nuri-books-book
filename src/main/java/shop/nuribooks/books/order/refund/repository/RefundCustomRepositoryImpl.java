package shop.nuribooks.books.order.refund.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.point.entity.child.QOrderSavingPoint;
import shop.nuribooks.books.order.order.entity.QOrder;
import shop.nuribooks.books.order.refund.dto.RefundInfoDto;
import shop.nuribooks.books.order.shipping.entity.QShipping;

@RequiredArgsConstructor
public class RefundCustomRepositoryImpl implements RefundCustomRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public RefundInfoDto findRefundInfo(Long orderId) {
		QOrder order = QOrder.order;
		QShipping shipping = QShipping.shipping;
		QOrderSavingPoint orderSavingPoint = QOrderSavingPoint.orderSavingPoint;

		return queryFactory.select(
				Projections.constructor(
					RefundInfoDto.class,
					order.paymentPrice,
					orderSavingPoint.amount,
					shipping.shippingAt
				))
			.from(order)
			.leftJoin(orderSavingPoint).on(orderSavingPoint.order.id.eq(orderId))
			.leftJoin(shipping).on(shipping.order.id.eq(orderId))
			.where(order.id.eq(orderId))
			.fetchOne();
	}
}
