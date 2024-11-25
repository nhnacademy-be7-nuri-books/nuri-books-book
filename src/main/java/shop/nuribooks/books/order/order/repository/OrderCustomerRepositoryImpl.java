package shop.nuribooks.books.order.order.repository;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Pageable;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.order.order.dto.request.OrderListPeriodRequest;
import shop.nuribooks.books.order.order.dto.response.OrderListResponse;
import shop.nuribooks.books.order.order.dto.response.OrderPageResponse;
import shop.nuribooks.books.order.order.entity.QOrder;
import shop.nuribooks.books.order.orderdetail.entity.OrderState;
import shop.nuribooks.books.order.orderdetail.entity.QOrderDetail;
import shop.nuribooks.books.order.shipping.entity.QShipping;

@RequiredArgsConstructor
public class OrderCustomerRepositoryImpl implements OrderCustomerRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public OrderPageResponse findOrders(boolean includeOrdersInPendingStatus, Long userId,
		Pageable pageable, OrderListPeriodRequest orderListPeriodRequest) {

		QOrder order = QOrder.order;
		QShipping shipping = QShipping.shipping;
		QOrderDetail orderDetail = QOrderDetail.orderDetail;

		NumberPath<Long> customerId = order.customer.id;

		BooleanBuilder whereClause = new BooleanBuilder();

		// 특정 사용자만
		whereClause.and(customerId.eq(userId));

		// 환불 취소는 제외
		whereClause.and(orderDetail.orderState.ne(OrderState.RETURNED));
		whereClause.and(orderDetail.orderState.ne(OrderState.CANCELED));

		// 기간 지정
		whereClause.and(order.orderedAt.between(
			orderListPeriodRequest.getStart().atStartOfDay(),
			orderListPeriodRequest.getEnd().atTime(LocalTime.MAX)));
		
		if (!includeOrdersInPendingStatus) {
			whereClause.and(orderDetail.orderState.ne(OrderState.PENDING));
		}

		List<OrderListResponse> orders = queryFactory.select(
				Projections.constructor(
					OrderListResponse.class,
					order.id,
					order.orderedAt,
					order.title,
					order.paymentPrice,
					shipping.orderInvoiceNumber,
					orderDetail.orderState
				)
			).from(order)
			.distinct()
			.leftJoin(shipping).on(shipping.order.eq(order))
			.leftJoin(orderDetail).on(orderDetail.order.eq(order))
			.where(whereClause)
			.orderBy(order.orderedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long totalCount = queryFactory.select(
				order.count()
			)
			.from(order)
			.leftJoin(shipping).on(shipping.order.eq(order))
			.leftJoin(orderDetail).on(orderDetail.order.eq(order))
			.where(whereClause)
			.fetchOne();

		return new OrderPageResponse(orders, totalCount);
	}
}
