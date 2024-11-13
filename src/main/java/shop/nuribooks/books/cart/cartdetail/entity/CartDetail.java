package shop.nuribooks.books.cart.cartdetail.entity;

import static jakarta.persistence.FetchType.*;

import java.time.LocalDateTime;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AccessLevel;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "cart_id")
	private Cart cart;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "book_id")
	private Book book;

	private int quantity;

	@Builder
	public CartDetail(Cart cart, Book book, int quantity) {
		this.cart = cart;
		this.book = book;
		this.quantity = quantity;
	}
}
