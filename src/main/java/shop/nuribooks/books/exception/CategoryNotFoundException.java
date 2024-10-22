package shop.nuribooks.books.exception;

public class CategoryNotFoundException extends ResourceNotFoundException {
	public CategoryNotFoundException(String message) {
		super(message);
	}
}
