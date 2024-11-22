package shop.nuribooks.books.exception.publisher;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;

public class PublisherAlreadyExistsException extends ResourceAlreadyExistException {
	public PublisherAlreadyExistsException() {
		super("출판사가 이미 등록되어 있습니다.");
	}
}
