package shop.nuribooks.books.exception.tag;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class TagNotFoundException extends ResourceNotFoundException {
	public TagNotFoundException() {
		super("태그가 존재하지 않습니다.");
	}
}
