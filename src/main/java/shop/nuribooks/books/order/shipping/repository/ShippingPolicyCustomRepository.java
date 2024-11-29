package shop.nuribooks.books.order.shipping.repository;

import shop.nuribooks.books.order.shipping.entity.ShippingPolicy;

public interface ShippingPolicyCustomRepository {
	ShippingPolicy findClosedShippingPolicy(int cost);
}
