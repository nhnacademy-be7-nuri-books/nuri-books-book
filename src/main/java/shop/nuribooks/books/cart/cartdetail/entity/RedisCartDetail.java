package shop.nuribooks.books.cart.cartdetail.entity;

import lombok.Getter;

@Getter
public class RedisCartDetail {
	private String bookId;
	private int quantity;

	public RedisCartDetail(String bookId, int quantity) {
		this.bookId = bookId;
		this.quantity = quantity;
	}
}
