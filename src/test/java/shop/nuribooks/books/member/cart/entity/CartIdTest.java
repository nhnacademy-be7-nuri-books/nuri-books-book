package shop.nuribooks.books.member.cart.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CartIdTest {

	@DisplayName("CartId의 equals() 메서드 테스트")
	@Test
	void testEquals() {
		CartId cartId1 = new CartId(1L, 1L);
		CartId cartId2 = new CartId(1L, 1L);

		assertThat(cartId1).isEqualTo(cartId2);
	}

	@DisplayName("CartId의 hashCode() 메서드 테스트")
	@Test
	void testHashCode() {
		CartId cartId1 = new CartId(1L, 1L);
		CartId cartId2 = new CartId(1L, 1L);

		assertThat(cartId1.hashCode()).isEqualTo(cartId2.hashCode());
	}

	@DisplayName("CartId의 equals() 메서드에서 서로 다른 값 테스트")
	@Test
	void testNotEqual() {
		CartId cartId1 = new CartId(1L, 1L);
		CartId cartId2 = new CartId(2L, 1L);

		assertThat(cartId1).isNotEqualTo(cartId2);
	}
}