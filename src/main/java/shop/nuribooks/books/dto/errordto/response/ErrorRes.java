package shop.nuribooks.books.dto.errordto.response;

public record ErrorRes(int statusCode, String message, String details) {
}
