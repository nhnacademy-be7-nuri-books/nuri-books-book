package shop.nuribooks.books.order.shipping.service;

import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.shipping.dto.ShippingRegisterRequest;

/**
 * 배송지 서비스 인터페이스
 *
 * @author nuri
 */
public interface ShippingService {
	void registerShipping(Order order, ShippingRegisterRequest shippingRegisterRequest);
}
