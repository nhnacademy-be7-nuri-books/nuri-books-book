package shop.nuribooks.books.exception.cart.customer;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class CustomerCartNotFoundException extends ResourceNotFoundException {
	public CustomerCartNotFoundException() {
		super("비회원 장바구니를 찾을 수 없습니다.");
	}

}
