package shop.nuribooks.books.exception.publisher;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class PublisherNotFoundException extends ResourceNotFoundException {
	public PublisherNotFoundException() {
		super("출판사가 존재하지 않습니다.");
	}
}
