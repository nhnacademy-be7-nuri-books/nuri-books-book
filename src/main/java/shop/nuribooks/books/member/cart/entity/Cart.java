package shop.nuribooks.books.member.cart.entity;

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
import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.member.member.entity.Member;

/**
 * @author Jprotection
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

	@EmbeddedId
	private CartId id;

	@ManyToOne(fetch = LAZY)
	@MapsId
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = LAZY)
	@MapsId
	@JoinColumn(name = "book_id")
	private Book book;

	private int quantity;

	private LocalDateTime updatedAt;

	/**
	 * 동일한 도서에 대해서 장바구니 생성 요청이 오면 도서의 수량을 변화시킨다.
	 */
	public void updateQuantity(int count) {
		quantity += count;
		updatedAt = LocalDateTime.now();
	}
}
