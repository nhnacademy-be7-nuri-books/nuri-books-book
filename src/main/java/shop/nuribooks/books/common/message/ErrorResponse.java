package shop.nuribooks.books.common.message;

public record ErrorResponse(int statusCode, String message, String details) {
}
