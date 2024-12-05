package shop.nuribooks.books.order.refund.repository;

import shop.nuribooks.books.order.refund.dto.RefundInfoDto;

public interface RefundCustomRepository {
	/**
	 * select
	 * from
	 * where
	 */
	RefundInfoDto findRefundInfo(Long orderId);
}
