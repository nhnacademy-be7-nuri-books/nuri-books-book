package shop.nuribooks.books.exception.order;

import org.springframework.web.client.HttpClientErrorException;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class NoStockAvailableException extends ResourceNotFoundException {
	public NoStockAvailableException(){
		super("재고가 부족합니다.");
	}
}
