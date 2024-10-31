package shop.nuribooks.books.member.cart.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Cart의 복합키를 정의하는 Embeddable Class
 * @author Jprotection
 */
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CartId implements Serializable {

	private Long memberId;
	private Long bookId;
}
