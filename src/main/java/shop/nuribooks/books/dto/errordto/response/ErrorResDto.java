package shop.nuribooks.books.dto.errordto.response;

public record ErrorResDto(int statusCode, String message, String details) {
}
