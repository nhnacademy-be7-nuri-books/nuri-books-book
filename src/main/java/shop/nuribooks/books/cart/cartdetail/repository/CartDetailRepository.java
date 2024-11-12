package shop.nuribooks.books.cart.cartdetail.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.cart.cartdetail.entity.CartDetail;
import shop.nuribooks.books.cart.cartdetail.entity.CartDetailId;

public interface CartDetailRepository extends JpaRepository<CartDetail, CartDetailId> {
	List<CartDetail> findByCart_Id(String cartId);
}
