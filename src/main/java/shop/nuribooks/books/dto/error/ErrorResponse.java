package shop.nuribooks.books.dto.error;

public record ErrorResponse(int statusCode, String message, String details) {
}
