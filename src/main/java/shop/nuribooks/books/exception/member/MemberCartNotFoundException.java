package shop.nuribooks.books.exception.member;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class MemberCartNotFoundException extends ResourceNotFoundException {
	public MemberCartNotFoundException() {
		super("장바구니에 해당 도서가 존재하지 않습니다.");
	}
}
