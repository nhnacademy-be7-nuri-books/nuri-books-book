package shop.nuribooks.books.exception.book;

public class AladinApiException extends RuntimeException {
	public AladinApiException() {
		super("Failed to retrieve new books from Aladin");
	}
}
