package shop.nuribooks.books.exception.order;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class NoStockAvailableException extends ResourceNotFoundException {
	public NoStockAvailableException(String title) {
		super(String.format("%s - 재고가 부족합니다.", title));
	}
}
