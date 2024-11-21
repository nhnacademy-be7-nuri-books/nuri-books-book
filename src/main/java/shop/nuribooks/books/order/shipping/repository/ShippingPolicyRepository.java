package shop.nuribooks.books.order.shipping.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.order.shipping.entity.ShippingPolicy;

public interface ShippingPolicyRepository extends JpaRepository<ShippingPolicy, Long>, ShippingPolicyCustomRepository {
	Page<ShippingPolicy> findAll(Pageable pageable);
}
