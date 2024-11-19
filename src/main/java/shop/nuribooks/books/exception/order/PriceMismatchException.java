package shop.nuribooks.books.exception.order;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class PriceMismatchException extends ResourceNotFoundException {
		public PriceMismatchException(){
			super("가격이 불일치합니다.");
		}
}