package shop.nuribooks.books.order.wrapping.exception;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class WrappingPaperNotFoundException extends ResourceNotFoundException {
	public WrappingPaperNotFoundException() {
		super("포장지가 존재하지 않습니다.");
	}
}
