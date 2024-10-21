package shop.nuribooks.books.exception;

public class ResourceNotFoundException extends RuntimeException {
	public ResourceNotFoundException(String resourceName, Long resourceId) {
		super(resourceName + " with ID " + resourceId + " not found.");
	}
}
