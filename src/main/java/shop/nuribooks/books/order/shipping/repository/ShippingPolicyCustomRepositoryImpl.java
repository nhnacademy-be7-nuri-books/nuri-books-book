package shop.nuribooks.books.order.shipping.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.order.shipping.entity.QShippingPolicy;
import shop.nuribooks.books.order.shipping.entity.ShippingPolicy;

@RequiredArgsConstructor
public class ShippingPolicyCustomRepositoryImpl implements ShippingPolicyCustomRepository {

	private final JPAQueryFactory queryFactory;

	/**
	 * 배송비 정책 조회
	 *
	 * <p>
	 * 최소 적용 금액이 주문 총 금액 이하이며,
	 * 적용 기간이 null(현재 적용중)인
	 * 정책을 중애 차이값이 가장 적은 1개의 값을 조회
	 * </p>
	 *
	 * @param cost
	 * @return
	 */
	@Override
	public ShippingPolicy findClosedShippingPolicy(int cost) {

		QShippingPolicy shippingPolicy = QShippingPolicy.shippingPolicy;

		return queryFactory.select(shippingPolicy)
			.from(shippingPolicy)
			.where(shippingPolicy.minimumOrderPrice.loe(cost)
				.and(shippingPolicy.expiration.isNull()))
			.orderBy(shippingPolicy.minimumOrderPrice.subtract(cost).abs().asc())
			.limit(1)
			.fetchOne();
	}
}
