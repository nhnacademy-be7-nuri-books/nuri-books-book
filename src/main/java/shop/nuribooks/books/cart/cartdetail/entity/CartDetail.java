package shop.nuribooks.books.cart.cartdetail.entity;

import static jakarta.persistence.FetchType.*;

import java.time.LocalDateTime;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.cart.entity.Cart;

/**
 * @author Jprotection
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDetail {

	@EmbeddedId
	private CartDetailId id;

	@ManyToOne(fetch = LAZY)
	@MapsId("cartId")
	@JoinColumn(name = "cart_id")
	private Cart cart;

	@ManyToOne(fetch = LAZY)
	@MapsId("bookId")
	@JoinColumn(name = "book_id")
	private Book book;

	private int quantity;

	/**
	 * 동일한 도서의 장바구니 수량을 증가시킨다.
	 */
	public void addQuantity(int quantity) {
		this.quantity += quantity;
	}

	/**
	 * 동일한 도서의 장바구니 수량을 변경한다.
	 */
	public void updateQuantity(int quantity) {
		this.quantity = quantity;
	}
}
