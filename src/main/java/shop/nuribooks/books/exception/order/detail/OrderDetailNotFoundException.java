package shop.nuribooks.books.exception.order.detail;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class OrderDetailNotFoundException extends ResourceNotFoundException {
	public OrderDetailNotFoundException() {
		super("해당 주문 상세를 찾을 수 없습니다.");
	}
}
