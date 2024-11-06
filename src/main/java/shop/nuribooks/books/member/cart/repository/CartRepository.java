package shop.nuribooks.books.member.cart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.member.cart.entity.Cart;
import shop.nuribooks.books.member.cart.entity.CartId;

/**
 * @author Jprotection
 */
public interface CartRepository extends JpaRepository<Cart, CartId> {

	List<Cart> findAllByMemberId(Long memberId);
}
