package shop.nuribooks.books.member.cart.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

/**
 * Cart의 복합키를 정의하는 Embeddable Class
 * @author Jprotection
 */
@Embeddable
@EqualsAndHashCode
public class CartId implements Serializable {

	private Long memberId;
	private Long bookId;
}
