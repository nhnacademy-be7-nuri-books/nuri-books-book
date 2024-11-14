package shop.nuribooks.books.order.shipping.repository;

import java.math.BigDecimal;

import shop.nuribooks.books.order.shipping.entity.ShippingPolicy;

public interface ShippingPolicyCustomRepository {
	ShippingPolicy findClosedShippingPolicy(int cost);
}
