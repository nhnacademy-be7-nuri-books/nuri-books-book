package shop.nuribooks.books.order.shipping.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.order.shipping.entity.Shipping;

public interface ShippingRepository extends JpaRepository<Shipping, Long> {


}
