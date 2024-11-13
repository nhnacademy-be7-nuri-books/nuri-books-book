package shop.nuribooks.books.cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.cart.entity.Cart;

public interface DBCartRepository extends JpaRepository<Cart, Long> {
	Optional<Cart> findByMember_Id(Long memberId);
}
