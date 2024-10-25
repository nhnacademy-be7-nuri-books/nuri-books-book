package shop.nuribooks.books.exception.address;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class AddressNotFoundException extends ResourceNotFoundException {
	public AddressNotFoundException() {
		super("주소를 찾을 수 없습니다.");
	}
}
