package shop.nuribooks.books.member.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.member.cart.entity.Cart;
import shop.nuribooks.books.member.cart.entity.CartId;

/**
 * @author Jprotection
 */
public interface CartRepository extends JpaRepository<Cart, CartId> {
}
