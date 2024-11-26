package shop.nuribooks.books.exception.order;

import shop.nuribooks.books.exception.BadRequestException;

public class OrderPriceValidationFailException extends BadRequestException {
	public OrderPriceValidationFailException() {
		super("클라이언트에서 넘겨받은 결제 금액과 실제 결제 금액이 다릅니다.");
	}
}
