package shop.nuribooks.books.exception.booklike;

public class ResourceAlreadyExistBookLikeIdException extends RuntimeException {
	public ResourceAlreadyExistBookLikeIdException() {
		super("이미 좋아요를 누른 도서입니다.");
	}
}
