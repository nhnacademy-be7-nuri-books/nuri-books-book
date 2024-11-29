package shop.nuribooks.books.order.orderdetail.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.entity.QBook;
import shop.nuribooks.books.order.order.entity.QOrder;
import shop.nuribooks.books.order.orderdetail.dto.OrderDetailItemDto;
import shop.nuribooks.books.order.orderdetail.dto.OrderDetailItemPageDto;
import shop.nuribooks.books.order.orderdetail.entity.OrderDetail;
import shop.nuribooks.books.order.orderdetail.entity.QOrderDetail;

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

	@Override
	public OrderDetailItemPageDto findOrderDetail(Long orderId, Pageable pageable) {

		QOrder order = QOrder.order;
		QBook book = QBook.book;
		QOrderDetail orderDetail = QOrderDetail.orderDetail;

		List<OrderDetailItemDto> orderDetailItems = queryFactory.select(
				Projections.constructor(
					OrderDetailItemDto.class,
					order.title,
					book.price,
					book.thumbnailImageUrl,
					orderDetail.unitPrice,
					orderDetail.count,
					orderDetail.orderState,
					orderDetail.isWrapped
				)
			).from(orderDetail)
			.leftJoin(order).on(order.id.eq(orderDetail.order.id))
			.leftJoin(book).on(book.id.eq(orderDetail.book.id))
			.where(order.id.eq(orderId))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long totalCount = queryFactory.select(
				orderDetail.count()
			).from(orderDetail)
			.leftJoin(order).on(order.eq(orderDetail.order))
			.leftJoin(book).on(book.eq(orderDetail.book))
			.where(order.id.eq(orderId))
			.fetchOne();

		return new OrderDetailItemPageDto(orderDetailItems, totalCount);
	}
}
