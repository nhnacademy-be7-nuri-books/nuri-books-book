package shop.nuribooks.books.order.orderDetail.repository;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.order.orderDetail.entity.OrderDetail;
import shop.nuribooks.books.order.orderDetail.entity.QOrderDetail;
import shop.nuribooks.books.order.shipping.entity.QShippingPolicy;
import shop.nuribooks.books.order.shipping.entity.ShippingPolicy;

@RequiredArgsConstructor
public class OrderDetailCustomRepositoryImpl implements OrderDetailCustomRepository {

	private final JPAQueryFactory queryFactory;

	/**
	 * 주문 아이디로 주문 상세 리스트 조회
	 *
	 * @param orderId 주문 아이디
	 * @return 주문 상세 리스트
	 */
	@Override
	public List<OrderDetail> findAllByOrderId(Long orderId) {
		QOrderDetail orderDetail = QOrderDetail.orderDetail;

		return queryFactory.selectFrom(orderDetail)
			.where(orderDetail.order.id.eq(orderId))
			.fetch();
	}
}
