package shop.nuribooks.books.cart.cartdetail.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.cart.cartdetail.entity.CartDetail;
import shop.nuribooks.books.cart.entity.Cart;

public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {
	Optional<List<CartDetail>> findAllByCart_Id(Long cartId);
	void deleteByCart(Cart cart);
}
