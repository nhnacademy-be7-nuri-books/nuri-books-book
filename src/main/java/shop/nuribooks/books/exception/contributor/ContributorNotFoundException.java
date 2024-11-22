package shop.nuribooks.books.exception.contributor;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class ContributorNotFoundException extends ResourceNotFoundException {
	public ContributorNotFoundException() {
		super("해당 기여자가 존재하지 않습니다.");
	}

}
