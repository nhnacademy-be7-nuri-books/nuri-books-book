package shop.nuribooks.books.exception.book;

import shop.nuribooks.books.exception.BadRequestException;

public class InvalidStockException extends BadRequestException {
	public InvalidStockException() {
		super("재고가 부족합니다.");
	}
}
