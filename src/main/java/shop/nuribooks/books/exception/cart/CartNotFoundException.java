package shop.nuribooks.books.exception.cart;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class CartNotFoundException extends ResourceNotFoundException {
	public CartNotFoundException() {
		super("장바구니를 찾을 수 없습니다.");
	}

}
